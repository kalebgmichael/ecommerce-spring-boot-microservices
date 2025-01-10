package com.productcnit.inventoryservice.service;

import com.productcnit.inventoryservice.dto.InventoryResponse;
import com.productcnit.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
//    public boolean isInStock(String skuCode)
//    {
//     return inventoryRepository.findBySkuCode(skuCode).isPresent();
//    }
//}
    public List<InventoryResponse> isInStock(List<String> skuCode)
    {
        // for making the system slow so that we can simulate the timelimiter from circuitbreaker library
//        log.info("wait started");
//        Thread.sleep(10000);
//        log.info("wait Ended");
        // the simulation ends here we can remove it later
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity()>0)
                            .build()
                ).toList();// map each inventory to inventory response object
    }
}