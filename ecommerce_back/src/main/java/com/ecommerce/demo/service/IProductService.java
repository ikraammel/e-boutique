package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.ProductDto;
import com.ecommerce.demo.models.Category;
import com.ecommerce.demo.models.Product;
import com.ecommerce.demo.models.SizeClothing;
import com.ecommerce.demo.models.SizePants;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);
    ProductDto createProduct(String name,
                             String description,
                             Double price,
                             Integer stock,
                             List<MultipartFile> files,
                             Category category,
                             String color,
                             List<SizeClothing> sizeClothing,
                             List<SizePants> sizePants);
    ProductDto updateProduct(Integer id,
                             String name,
                             String description,
                             Double price,
                             Integer stock,
                             Category category);
    void deleteProduct(Integer id);
    List<Product> searchProductsByName(String name);
    List<ProductDto> getProductsByCategory(Category category);
    void deleteProductImageByIndex(Integer productId, Integer variantId, int index);
}
