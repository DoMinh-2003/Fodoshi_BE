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
    
    // Sửa lỗi: Lưu product trước để có ID
    product.setProductCondition(productDTO.getCondition());
    product.setSize(productDTO.getSize());
    product.setColor(productDTO.getColor());
    product.setStatus(productDTO.getProductStatus());
    product.setOriginalPrice(productDTO.getOriginalPrice());
    product.setSellingPrice(productDTO.getSellingPrice());
    product.setMainImage(productDTO.getMainImage());
    product.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
    product.setGender(productDTO.getGender());
    
    // Add this line to set the deleted field to false
    product.setDeleted(false);

    if(productDTO.getProductStatus().equals(ProductStatus.PENDING)){
        product.setConsignor(user);
    } else {
        if (productDTO.getConsignorId() == null) {
            // If no consignorId is provided, use current user
            product.setConsignor(user);
        } else {
            product.setConsignor(userRepository.findById(productDTO.getConsignorId())
                .orElseThrow(() -> new NotFoundException("user not found")));
        }
    }
    
    // Lưu product trước khi tạo relationships
    Product savedProduct = productRepository.save(product);
    
    // Thêm categories
    if (productDTO.getCategory() != null) {
        for(Long id: productDTO.getCategory()) {
            Category category = categoryService.getCategoryById(id);
            savedProduct.getCategories().add(category);
        }
    }
    
    // Thêm brands
    if (productDTO.getBrand() != null) {
        for(Long id: productDTO.getBrand()) {
            Brand brand = brandService.getBrandById(id);
            savedProduct.getBrands().add(brand);
        }
    }
    
    // Thêm images
    if (productDTO.getImageUrls() != null) {
        for(String url: productDTO.getImageUrls()) {
            Image image = new Image();
            image.setImage(url);
            image.setProduct(savedProduct);
            image.setImage(url); // Chắc chắn thiết lập đúng trường URL
            imageRepository.save(image);
        }
    }
    
    // Thêm tags
    if (productDTO.getTags() != null) {
        for(String name: productDTO.getTags()) {
            Tag tag = new Tag();
            tag.setTagName(name);
            tag.getProducts().add(savedProduct);
            tagRepository.save(tag);
            savedProduct.getTags().add(tag);
        }
    }
    
    // Tạo product history sau khi đã lưu product
    ProductHistory productHistory = new ProductHistory();
    productHistory.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
    productHistory.setProduct(savedProduct);
    productHistory.setStatus(productDTO.getProductStatus().name());
    productHistoryRepository.save(productHistory);
    
    // Lưu product lần cuối với tất cả mối quan hệ
    return productRepository.save(savedProduct);
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
            productHistory.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
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
        productHistory.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        productHistory.setProduct(product);
        productHistory.setStatus(status.getStatus().name());

        product.getProductHistories().add(productHistory);
        productHistoryRepository.save(productHistory);
        return productRepository.save(product);
    }



    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategories_NameContainingIgnoreCaseOrTags_TagNameContainingIgnoreCase(
                keyword, keyword, keyword, keyword);
    }

}
