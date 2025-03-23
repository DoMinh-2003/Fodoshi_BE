package com.BE.service.implementServices;

import com.BE.enums.ProductStatus;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.*;
import com.BE.model.request.ProductRequestDTO;
import com.BE.model.request.ProductStatusRequest;
import com.BE.repository.*;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateNowUtils dateNowUtils;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    ProductHistoryRepository productHistoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductRequestDTO productDTO) {
        User user = AccountUtils.getCurrentUser();
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        for(Long id: productDTO.getCategory()){
            Category category = categoryService.getCategoryById(id);
            category.getProducts().add(product);
            product.getCategories().add(category);
        }
        for(Long id: productDTO.getBrand()){
            Brand brand = brandService.getBrandById(id);
            brand.getProducts().add(product);
            product.getBrands().add(brand);
        }
        product.setProductCondition(productDTO.getCondition());
        product.setSize(productDTO.getSize());
        product.setColor(productDTO.getColor());
        for(String url: productDTO.getImageUrls()){
            Image image = new Image();
            image.setImage(url);
            image.setProduct(product);
            product.getImageUrls().add(image);
        }
        for(String name: productDTO.getTags()){
            Tag tag = new Tag();
            tag.setTagName(name);
            tag.getProducts().add(product);
            product.getTags().add(tag);
        }
        product.setStatus(productDTO.getProductStatus());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setMainImage(productDTO.getMainImage());
        product.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        product.setGender(productDTO.getGender());

        ProductHistory productHistory = new ProductHistory();
        productHistory.setCreatedAt(dateNowUtils.dateNow());
        productHistory.setProduct(product);
        productHistory.setStatus(productDTO.getProductStatus().name());

        product.getProductHistories().add(productHistory);
        productHistoryRepository.save(productHistory);

        if(productDTO.getProductStatus().equals(ProductStatus.PENDING)){
            product.setConsignor(user);
        }else{
            product.setConsignor(userRepository.findById(productDTO.getConsignorId()).orElseThrow(() -> new NotFoundException("user not found")));
        }



        return productRepository.save(product);
    }


    public Product updateProduct(Long id, ProductRequestDTO productDTO) {
        Product product = getProductById(id);
        User user = AccountUtils.getCurrentUser();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.getCategories().clear();
        product.getBrands().clear();
        for(Long idCategory: productDTO.getCategory()){
            Category category = categoryService.getCategoryById(idCategory);
            category.getProducts().add(product);
            product.getCategories().add(category);
        }
        for(Long idBrand: productDTO.getBrand()){
            Brand brand = brandService.getBrandById(idBrand);
            brand.getProducts().add(product);
            product.getBrands().add(brand);
        }
        product.setProductCondition(productDTO.getCondition());
        product.setSize(productDTO.getSize());
        product.setColor(productDTO.getColor());
        product.setMainImage(productDTO.getMainImage());
        List<Image> images = imageRepository.findAllByProductId(product.getId());
        imageRepository.deleteAll(images);
        List<Tag> tags = tagRepository.findAllByProductsId(product.getId());
        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        tagRepository.deleteTagAssociations(tagIds);
        tagRepository.deleteAll(tags);
        for(String url: productDTO.getImageUrls()){
            Image image = new Image();
            image.setImage(url);
            image.setProduct(product);
            product.getImageUrls().add(image);
        }
        for(String name: productDTO.getTags()){
            Tag tag = new Tag();
            tag.setTagName(name);
            tag.getProducts().add(product);
            product.getTags().add(tag);
        }
        product.setStatus(productDTO.getProductStatus());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        product.setGender(productDTO.getGender());


        if(!product.getStatus().equals(productDTO.getProductStatus())){
            ProductHistory productHistory = new ProductHistory();
            productHistory.setCreatedAt(dateNowUtils.dateNow());
            productHistory.setProduct(product);
            productHistory.setStatus(productDTO.getProductStatus().name());

            product.getProductHistories().add(productHistory);
            productHistoryRepository.save(productHistory);
        }



        if(productDTO.getProductStatus().equals(ProductStatus.PENDING)){
            product.setConsignor(user);
        }else{
            product.setConsignor(userRepository.findById(productDTO.getConsignorId()).orElseThrow(() -> new NotFoundException("user not found")));
        }


        return productRepository.save(product);
    }



        public List<Product> getProductByStatus(ProductStatus statusRequest) {
        return  productRepository.findAllByStatus(statusRequest);
    }


    public List<Product> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandsId(brandId);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoriesId(categoryId);
    }



    public Product deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setStatus(ProductStatus.REMOVED);
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));
    }


    public List<Product> getProductByConsignor() {
        User user = accountUtils.getCurrentUser();
        return productRepository.findAllByConsignorId(user.getId());
    }

    public Product changeStatus(Long id, ProductStatusRequest status) {
        Product product = getProductById(id);
        product.setStatus(status.getStatus());

        ProductHistory productHistory = new ProductHistory();
        productHistory.setCreatedAt(dateNowUtils.dateNow());
        productHistory.setProduct(product);
        productHistory.setStatus(status.getStatus().name());

        product.getProductHistories().add(productHistory);
        productHistoryRepository.save(productHistory);
        return productRepository.save(product);
    }
}
