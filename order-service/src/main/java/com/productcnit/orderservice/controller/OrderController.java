package com.productcnit.orderservice.controller;

import com.productcnit.orderservice.dto.OrderRequest;
import com.productcnit.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor

public class OrderController {

    private final OrderService orderService;
    @PostMapping("/placeOrder")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethod") // the name is the same as the one in the properties so inventory
    @TimeLimiter(name="inventory")
    @Retry(name = "inventory")
    // when ever the circuit breaker understands there is a failure it will
    // execute the fallback method
//    public String placeOrder(@RequestBody OrderRequest orderRequest)
//    {
//        orderService.placeOrder(orderRequest);
//        return "Order placed successfully";
//    }
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest)
    {
       // execute the placeorder in a separate thread so we call the supplyasync
       return CompletableFuture.supplyAsync(()->  orderService.placeOrder(orderRequest));
    }

    // the return type and the parameter have to be the same with the method
    //where we are applying the circuit breaker in ourcase placeorder
    // we will take also the runtimeexception from the palce order and return a message

  //public String fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException)
//    {
//        return " Oops something went wrong please order later";
//    }

    // since the timelimiter will make an asynchronous message we have to change the return type
    // from string to completablefuture<string>
    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException)
    {
        return CompletableFuture.supplyAsync(()->" Oops something went wrong please order later") ;
    }

    @GetMapping("/response")
    @ResponseStatus(HttpStatus.OK)
    public String getAllProducts1()
    {
        return "response from order-service";

    }
}
