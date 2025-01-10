package com.productcnit.orderservice.service;

import com.productcnit.orderservice.dto.InventoryResponse;
import com.productcnit.orderservice.dto.OrderLineItemsDto;
import com.productcnit.orderservice.dto.OrderRequest;
import com.productcnit.orderservice.event.OrderPlacedEvent;
import com.productcnit.orderservice.model.Order;
import com.productcnit.orderservice.model.OrderLIneItems;
import com.productcnit.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor //  crate the contsturctur based on the parameters during compile time
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    public String placeOrder(OrderRequest orderRequest)
    {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLIneItems> orderLIneItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                //.map(orderLineItemsDto -> mapToDto(orderLineItemsDto)) so replace this with method reference this::mapToDto
                .map(this::mapToDto)
                .toList();// change the map to list

        order.setOrderLIneItemsList(orderLIneItemsList); // save the returned orderLineItems form the mapToDto method
        //call inventory service and place order if product is in stock

        //get the list of sku code form order
        List<String> skuCodes = order.getOrderLIneItemsList().stream()
                // .map(orderLIneItems -> orderLIneItems.getSkuCode()), replace it with method reference as below
                .map(OrderLIneItems::getSkuCode)
                .toList();

        // create a span name because the inventory service is running on a different thread
        // due to the circuit breaker and on zipkin we can find only api gateway then order but not
        //inventory when makeing the api/order route
        // with this specific tracer by creating span name called inventoryservicelookup
        // we can now trace the call to inventory with from the apigateway to the order
        // so in this way we can create any span name put a code of block and trace it
         Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

         try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start()))
         {
             // make a call to the inventory service
             InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                     .uri("http://inventory-service/api/inventory",
                             uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()) //pass the list of skucodes
                     .retrieve()
                     .bodyToMono(InventoryResponse[].class) // return the response as an array of inventory response
                     .block();


             //inventoryResponsesArray.stream(), will result the following stream it  //inventoryResponse -> inventoryResponse.isInStock() is changed to InventoryResponse::isInStock
             boolean allproductsInStock=  Arrays.stream( inventoryResponsesArray)
                     .allMatch(InventoryResponse::isInStock);
             // so we have list of inventoryResponsesArray
             // we are converting to a stream
             // it will check with allMatch if all the proudcts in the array are true with isinstock method even if one is false it will return false


             if(allproductsInStock)
             {
                 orderRepository.save(order);
                 kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
                 return "Order placed successfully";
             }
             else {
                 throw new IllegalArgumentException("product is not in stock, please try again later");
             }

         }
         finally {
             inventoryServiceLookup.end();
         }


    }

    private OrderLIneItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLIneItems orderLIneItems = new OrderLIneItems();
        orderLIneItems.setPrice(orderLineItemsDto.getPrice());
        orderLIneItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLIneItems.setSkuCode(orderLineItemsDto.getSkuCode());// get the value from orderLineItemsDto and then set the value orderLIneItems of the model finally return the value
        return orderLIneItems; // return the list to the map
    }
    }


