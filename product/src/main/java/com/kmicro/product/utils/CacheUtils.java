package com.kmicro.product.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmicro.product.constants.AppConstants;
import com.kmicro.product.dtos.BoughtProductRecord;
import com.kmicro.product.dtos.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheUtils {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, ProductDTO> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;


    public void addProductListToCache(List<ProductDTO> productsList){
        Map<String, ProductDTO> productMap = productsList.stream()
                .collect(Collectors.toMap(product -> String.valueOf(product.getId()), product -> product));

        redisTemplate.opsForHash().putAll(
                AppConstants.CACHE_PREFIX_PRODUCT_LIST ,
                productMap);
        redisTemplate.expire(AppConstants.CACHE_PREFIX_PRODUCT_LIST , 10, TimeUnit.MINUTES);
    }

    public List<ProductDTO> getProductListFromCache(){
        List<Object> cachedProductsList =  redisTemplate.opsForHash().values(AppConstants.CACHE_PREFIX_PRODUCT_LIST );
        if(cachedProductsList.isEmpty()){
           return null;
        }
        return  cachedProductsList.stream()
                .map(obj -> (ProductDTO) obj) // Cast to CartDTO
                .collect(Collectors.toList());
    }

    public void updateProductListToCache(List<ProductDTO> productsList){
        List<Object> cachedProductsList =  redisTemplate.opsForHash().values(AppConstants.CACHE_PREFIX_PRODUCT_LIST );

        Map<String, ProductDTO> oldProductMap =  cachedProductsList.stream()
                .map(obj -> (ProductDTO) obj) // Cast to CartDTO
                .toList()
                .stream()
                .collect(Collectors.toMap(product -> String.valueOf(product.getId()), product -> product));

        Map<String, ProductDTO> updatedProductMap = productsList.stream()
                .collect(Collectors.toMap(product -> String.valueOf(product.getId()), product -> product));

        oldProductMap.putAll(updatedProductMap);

        redisTemplate.opsForHash().putAll(
                AppConstants.CACHE_PREFIX_PRODUCT_LIST ,
                oldProductMap);

        redisTemplate.expire(AppConstants.CACHE_PREFIX_PRODUCT_LIST , 10, TimeUnit.MINUTES);
    }

    public ProductDTO getSingleProductFromCache(Long id){
        Object cachedProduct =  redisTemplate.opsForHash().get(AppConstants.CACHE_PREFIX_PRODUCT_LIST , String.valueOf(id));
        if(cachedProduct  == null){
            return null;
        }
        return objectMapper.convertValue(cachedProduct,ProductDTO.class);
    }

    private void evictBoughtProductCaches(List<BoughtProductRecord> records) {
        Cache cache = cacheManager.getCache(AppConstants.CACHE_PREFIX_PRODUCT);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    if (cache != null) {
                        records.forEach(record -> {
                            cache.evict(record.id());
                            log.info("Post-Commit: Evicted product cache for ID: {}", record.id());
                        });
                    }
                }
            });
        } else {
            // Fallback for non-transactional calls
            if (cache != null) {
                records.forEach(record -> {
                    cache.evict(record.id());
                    log.info("Post-Commit: Evicted product cache for ID: {}", record.id());
                });
            }
        }
    }

    private void transactionAfterCommit(){
        // 2. Register a synchronization to evict cache ONLY after successful commit
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
//                    evictBoughtProductCaches(productRecords);
                }
            });
        } else {
            // Fallback for non-transactional calls
//            evictBoughtProductCaches(productRecords);
        }
    }

    private void updateGlobalProductCache(List<ProductDTO> newDtos) {
        String key = serviceName + ":" + AppConstants.CACHE_PREFIX_PRODUCT_LIST + ":all";

//        // Fetch existing list
//        List<ProductDTO> existingList = (List<ProductDTO>) redisTemplate.opsForValue().get(key);
//
//        if (existingList != null) {
//            // Add new items to the existing cached list
//            existingList.addAll(newDtos);
//            // Put back into Redis with original TTL
//            redisTemplate.opsForValue().set(key, existingList, 10, TimeUnit.MINUTES);
//            log.info("Manually appended {} products to 'all' cache", newDtos.size());
        }


}//EC


