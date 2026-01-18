package com.kmicro.product.service;

import com.kmicro.product.dtos.*;
import com.kmicro.product.entities.ProductEntity;
import com.kmicro.product.exception.DataNotExistException;
import com.kmicro.product.mapper.ProductMapper;
import com.kmicro.product.repository.ProductRepository;
import com.kmicro.product.repository.ProductSpecifications;
import com.kmicro.product.utils.CacheUtils;
import com.kmicro.product.utils.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CacheUtils cacheUtils;
//    private final Logger log = LoggerFactory.getLogger(ProductService.class);


    @Transactional(readOnly = true)
//    @Cacheable(value = AppConstants.CACHE_PREFIX_PRODUCT_LIST, key = " 'all' ")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> getCachedProductsList =  cacheUtils.getProductListFromCache() ;

       if(getCachedProductsList != null && getCachedProductsList.size() == productRepository.count()){
           log.info("Cache found");
           return getCachedProductsList;
       }

        List<ProductEntity> productEntities = productRepository.findAll();
        if(productEntities.isEmpty()){
            return Collections.emptyList();
        }
        List<ProductDTO> dtos = ProductMapper.mapEntityToDtoList(productEntities);
        cacheUtils.addProductListToCache(dtos);
        log.info("Caching Not Found. Caching DB Result for next time");
        return new ArrayList<>(dtos); // Ensure it is a standard ArrayList
//        return ProductMapper.mapEntityToDtoList(productEntities);
    }

//    @Caching(evict = {
//            @CacheEvict(value = AppConstants.CACHE_PREFIX_PRODUCT_LIST, key = " 'all' "),
//    })
    @Transactional
    public List<ProductDTO> addProduct(List<ProductDTO> productList) {
        // need to handle agr categoryID exist nahi, to product  add na ho

        List<ProductEntity> productEntityList = ProductMapper.mapDTOToProductEntityNew(productList);

        List<ProductEntity> savedEntityList = productRepository.saveAll(productEntityList);

        if(savedEntityList.size() < 0){
            log.info("Products not added, Failed");
            return Collections.emptyList();
        }
        log.info("Products added successful");
        List<ProductDTO> dtos = ProductMapper.mapEntityToDtoList(savedEntityList);
        cacheUtils.updateProductListToCache(dtos);
        return dtos;
    }

    /*
    *   Update Product Fail-Fast
    * */
    @Transactional
    public List<ProductDTO> updateProduct(List<ProductDTO> productList) {
        // need to handle agr categoryID exist nahi, to product  add na ho
        List<ProductEntity> resultEntities = new ArrayList<>();

        for (ProductDTO dto : productList) {
            ProductEntity entity;

            if (dto.getId() != null && dto.getId() > 0) {
                // 1. Fetch existing record to preserve fields not in DTO
                entity = productRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + dto.getId()));

                // 2. Only update fields provided in DTO (Manual or MapStruct)
                ProductMapper.updateEntityFromDto(dto, entity);
                resultEntities.add(entity);
            }
            else {
                 // 3. It's a brand new product
//                entity = ProductMapper.dtoToEntity(dto);
                throw new DataNotExistException("NO ID FOUND IN REQUESTED DATA");
            }
//
//            resultEntities.add(entity);
        }

        List<ProductEntity> savedEntityList = productRepository.saveAll(resultEntities);
        log.info("Products Updated Successfully");
        List<ProductDTO> dtos = ProductMapper.mapEntityToDtoList(savedEntityList);
        cacheUtils.updateProductListToCache(dtos);
        return dtos;
    }

    /*
    *   Update Product Fail-Safe
    * */
//    @Caching(evict = {
//            @CacheEvict(value = AppConstants.CACHE_PREFIX_PRODUCT_LIST, key = " 'all' "),
//    })
    @Transactional
    public BulkUpdateResponseRecord bulkUpdateProduct(List<ProductDTO> productList) {
        List<Long> successIds = new ArrayList<>();
        List<BulkErrorResponseRecord> errors = new ArrayList<>();
        List<ProductEntity> entitiesToSave = new ArrayList<>();

        for (ProductDTO dto : productList) {
            try {
                if (dto.getId() == null || dto.getId() <= 0) {
                    errors.add(new BulkErrorResponseRecord(null, "Invalid or missing ID"));
                    log.info("Product Fail-Safe -- Invalid or missing ID: {}",dto.getId());
                    continue;
                }

                Optional<ProductEntity> entityOpt = productRepository.findById(dto.getId());

                if (entityOpt.isPresent()) {
                    ProductEntity entity = entityOpt.get();
                    ProductMapper.updateEntityFromDto(dto, entity);
                    entitiesToSave.add(entity);
                    successIds.add(dto.getId());
                    log.info("Product Fail-Safe -- Product Entity Found & Updated {}",entity.getId());
                } else {
                    errors.add(new BulkErrorResponseRecord(dto.getId(), "Product ID not found in database"));
                    log.error("Product Fail-Safe -- Product ID not found in database: {}",dto.getId());
                }
            } catch (Exception e) {
                // Catch unexpected mapping errors for a specific row
                errors.add(new BulkErrorResponseRecord(dto.getId(), "Internal error: " + e.getMessage()));
                log.error("Product Fail-Safe -- Internal error:",e);
                log.debug("DEBUG: ", e.fillInStackTrace());
            }

        }
        if (!entitiesToSave.isEmpty()) {
           var savedEntityList = productRepository.saveAll(entitiesToSave);
            cacheUtils.updateProductListToCache(ProductMapper.mapEntityToDtoList(savedEntityList));
            log.info("Product Fail-Safe -- Product Updated Successful");
        }

        return new BulkUpdateResponseRecord(successIds, errors);
    }

    public void deleteProduct(Long id) {
        // check carts cache before delete
        // only admin can final delete
//        productRepository.deleteById(id);
    }

    public ProductDTO getProductById(Long id) {
        ProductDTO cachedProduct = cacheUtils.getSingleProductFromCache(id);
        if(cachedProduct != null){
            log.info("Cached Product FOUND");
            return cachedProduct;
        }
        Optional<ProductEntity> product = productRepository.findById(id);
        if(product.isEmpty()){
            throw  new DataNotExistException("Product not found:  "+id);
        }
        log.info("Cache Not FOUND, Saving DB result in Cache");
        ProductDTO dbResult  = ProductMapper.EntityToDTO(product.get());
        cacheUtils.updateProductListToCache(List.of(dbResult));
        return dbResult ;
    }

    public Slice<ProductEntity> filterAndSortedProductList() {
        int pageSize = 5;
        int pageNum = 1;
        String sortBy = "price";
        Pageable pageable = PageRequest.of(pageNum-1,pageSize, Sort.by(sortBy).descending());
      /*  Page<ProductEntity> pagedData = productRepository.findAll(pageable);
        System.out.println(pagedData.getTotalElements());
        System.out.println(pagedData.getTotalPages());
        System.out.println(pagedData);*/
        Slice<ProductEntity> slicedData = productRepository.findAll(pageable);
//        slicedData.nextOrLastPageable();
        return slicedData;
//        System.out.println(slicedData.getContent());
//        System.out.println(slicedData.getNumberOfElements());
//        System.out.println(slicedData.getPageable());
//        System.out.println(slicedData.getPageable());
//        return slicedData;
    }

    @Transactional(readOnly = true)
    public  PagedResponseDTO<ProductDTO> filterAndSortedProductList(String keyword, String category, Double minPrice, Double maxPrice, Pageable pageable){

        Pageable restrictedPageable = PaginationUtils.getPageRequest(pageable);

        Specification<ProductEntity> searchAndFilterSpec = ProductSpecifications.applySearchAndFilter(category, minPrice, maxPrice, keyword);

        Page<ProductEntity>paginatedResult =  productRepository.findAll(searchAndFilterSpec, restrictedPageable);
        log.info("Paginated Result ready....");
        List<ProductDTO> dtos = paginatedResult.stream().map(ProductMapper::EntityToDTO).toList();

       return PaginationUtils.toPagedResponse(paginatedResult, dtos);
    }

    @Transactional(readOnly = true)
    public boolean checkProductAvailability(Long id) {
        return  productRepository.existsById(id);
    }


    @Transactional
    public BulkUpdateResponseRecord changeQtyBoughtProduct(List<BoughtProductRecord> productRecords) {
        List<Long> successIds = new ArrayList<>();
        List<BulkErrorResponseRecord> errors = new ArrayList<>();
        List<ProductEntity> entitiesToSave = new ArrayList<>();

        for(var product : productRecords){
            try{
                Optional<ProductEntity> productOpt = productRepository.findById(product.id());

                // 1. check if product exists
                if(productOpt.isPresent()){
                    ProductEntity productEntity = productOpt.get();

                    // 2. check if qty > existingQty and existingQty-qty < 1
                    if(productEntity.getStockQuantity() > product.qty() && productEntity.getStockQuantity() - product.qty() > 1){

                        // 3. change existingQty
                        productEntity.setStockQuantity(productEntity.getStockQuantity() - product.qty());
                        entitiesToSave.add(productEntity);
                        successIds.add(productEntity.getId());
                        log.info("Bought Product ID: {} Quantity left: {}",productEntity.getId(), productEntity.getStockQuantity());
                    }else {
                        errors.add(new BulkErrorResponseRecord(productEntity.getId(),"Product is not in sufficient quantity: "+productEntity.getStockQuantity()));
                        log.info("Product is not in sufficient quantity: {}",productEntity.getStockQuantity());
                    }
                }else {
                    errors.add(new BulkErrorResponseRecord(product.id(),"Product ID not found in database"));
                    log.error("Product ID: {} not found in database",product.id());
                }
            } catch (Exception ex) {
                errors.add(new BulkErrorResponseRecord(product.id(),"Internal error: " + ex.getMessage()));
                log.error("Exception Occured in BoughtProduct:", ex);
//                log.debug(ex.printStackTrace());
            }
        }

        if(!entitiesToSave.isEmpty()){
            var savedEntityList = productRepository.saveAll(entitiesToSave);
            cacheUtils.updateProductListToCache(ProductMapper.mapEntityToDtoList(savedEntityList));
        }

        return new BulkUpdateResponseRecord(successIds, errors);
    }

    @Transactional
    public BulkUpdateResponseRecord changeQtyBoughtProductOptimized(List<BoughtProductRecord> productRecords){
        List<Long> successIds = new ArrayList<>();
        List<BulkErrorResponseRecord> errors = new ArrayList<>();
        List<ProductEntity> entitiesToSave = new ArrayList<>();

        //-- Query for Unique IDs
        Set<Long> productRecIdSet = productRecords.stream().map(BoughtProductRecord::id).collect(Collectors.toSet());
        Map<Long, ProductEntity> productIdAndEntityMap = productRepository.findByIdIn(productRecIdSet)
                .stream().collect(Collectors.toMap(ProductEntity::getId, productEntity ->productEntity));

        for (BoughtProductRecord record : productRecords) {
            ProductEntity product = productIdAndEntityMap.get(record.id());

            if (product == null) {
                errors.add(new BulkErrorResponseRecord(record.id(), "Product ID not found in database"));
                log.error("Product ID: {} not found in database", record.id());
                continue;
            }
            try{
                // 2. check if qty > existingQty and existingQty-qty < 1
                if(product.getStockQuantity() > record.qty() && product.getStockQuantity() - record.qty() > 1){
                    // 3. change existingQty
                    product.setStockQuantity(product.getStockQuantity() - record.qty());
                    entitiesToSave.add(product);
                    successIds.add(product.getId());
                    log.info("Bought Product ID: {} Quantity left: {}",product.getId(), product.getStockQuantity());
                }else {
                    errors.add(new BulkErrorResponseRecord(product.getId(),"Product is not in sufficient quantity: "+product.getStockQuantity()));
                    log.info("Product is not in sufficient quantity: {}",product.getStockQuantity());
                }

            } catch (Exception ex) {
                errors.add(new BulkErrorResponseRecord(record.id(),"Internal error: " + ex.getMessage()));
                log.error("Exception Occured in BoughtProduct:", ex);
//                log.debug(ex.printStackTrace());
            }
        }

        if(!entitiesToSave.isEmpty()){
            var savedEntityList = productRepository.saveAll(entitiesToSave);
            cacheUtils.updateProductListToCache(ProductMapper.mapEntityToDtoList(savedEntityList));
        }
        return new BulkUpdateResponseRecord(successIds, errors);
    }


    public ProductDTO checkProductAvailability(@Valid BoughtProductRecord productRecord) {
        ProductDTO product = getProductById(productRecord.id());
        if(product.getQuantity() > productRecord.qty() && product.getQuantity() - productRecord.qty() > 1){
            return product;
        }
        log.info("Product is not in sufficient quantity: {}",product.getQuantity());
        throw new DataNotExistException("Product is not in sufficient quantity: {}",product.getQuantity());
    }
}// endClass
