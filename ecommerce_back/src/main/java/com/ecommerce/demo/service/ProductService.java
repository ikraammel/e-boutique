package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.ProductDto;
import com.ecommerce.demo.dtos.ProductVariantDto;
import com.ecommerce.demo.exceptions.ProductNotFoundException;
import com.ecommerce.demo.mappers.ProductMapper;
import com.ecommerce.demo.models.*;
import com.ecommerce.demo.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto createProduct(String name,
                                    String description,
                                    Double price,
                                    Integer stock,
                                    List<MultipartFile> files,
                                    Category category,
                                    String color,
                                    List<SizeClothing> sizeClothing,
                                    List<SizePants> sizePants) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock != null ? stock : 0);
        product.setCategory(category);

        ProductVariant variant = new ProductVariant();
        variant.setColor(color);
        variant.setSizeClothings(sizeClothing);
        variant.setSizePants(sizePants);
        variant.setProduct(product);
        variant.setPrice(price);

        if (files != null && !files.isEmpty()) {
            List<ProductImage> images = files.stream()
                    .map(file -> {
                        try{
                            ProductImage img = new ProductImage();
                            img.setData(file.getBytes());
                            img.setVariant(variant);
                            return img;
                        } catch (IOException e){
                            throw new RuntimeException(e);
                        }
                    }).toList();
            variant.setImages(images);
        }

        product.setVariants(Set.of(variant));
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Integer id,
                                    String name,
                                    String description,
                                    Double price,
                                    Integer stock,
                                    Category category) {

        int updated = productRepository.updateProductFields(id, name, description, price, stock, category);

        if (updated == 0) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }

        // On récupère le produit mis à jour (sans LOBs chargés)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        return productMapper.toDto(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Product> searchProductsByName(String query){
        if (query == null || query.isBlank()){
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Category category){
        List<Product> products = productRepository.findByCategory(category);
        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }


    @Transactional
    public Product getProductByIdEntity(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    @Transactional
    public void deleteProductImageByIndex(Integer productId, Integer variantId, int index) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ProductVariant variant;
        if (variantId != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException("Variant not found"));
        } else {
            if (product.getVariants() == null || product.getVariants().isEmpty()) {
                throw new RuntimeException("No variants found for product");
            }
            variant = product.getVariants().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No variants found"));

        }

        List<ProductImage> images = variant.getImages();
        if (images == null || index < 0 || index >= images.size()) {
            throw new IndexOutOfBoundsException("Invalid image index");
        }

        ProductImage removed = images.remove(index);
        removed.setVariant(null); // détache de la variante
    }

    @Transactional
    public ProductDto addVariant(Integer productId,
                                 String color,
                                 double price,
                                 List<MultipartFile> files,
                                 List<SizeClothing> sizeClothings,
                                 List<SizePants> sizePants){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id :" +productId + " not found"));

        ProductVariant variant = new ProductVariant();
        variant.setColor(color);
        variant.setSizeClothings(sizeClothings);
        variant.setSizePants(sizePants);
        variant.setProduct(product);
        variant.setPrice(price);

        if (files != null && !files.isEmpty()){
            List<ProductImage> images = files.stream()
                    .map(file -> {
                        try{
                            ProductImage img = new ProductImage();
                            img.setData(file.getBytes());
                            img.setVariant(variant);
                            return img;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
            variant.setImages(images);
        }

        product.getVariants().add(variant);

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional
    public ProductDto updateVariant(Integer productId,
                                    Integer variantId,
                                    double price,
                                    String color,
                                    List<SizeClothing> sizeClothings,
                                    List<SizePants> sizePants){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id :" +productId + " not found"));

        ProductVariant variant = product.getVariants().stream()
                        .filter(v -> v.getId().equals(variantId))
                        .findFirst()
                        .orElseThrow(() -> new ProductNotFoundException("Variant with id " + variantId + " not found"));
        if (color != null) variant.setColor(color);
        if(price!= 0) variant.setPrice(price);
        if (sizeClothings != null) {
            variant.getSizeClothings().clear();
            for (SizeClothing s : sizeClothings) {
                variant.getSizeClothings().add(s);
            }
            variant.getSizePants().clear(); // on vide l'autre catégorie
        } else if (sizePants != null) {
            variant.getSizePants().clear();
            for (SizePants s : sizePants) {
                variant.getSizePants().add(s);
            }
            variant.getSizeClothings().clear(); // on vide l'autre catégorie
        }
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional
    public void addVariantImages(Integer variantId, List<MultipartFile> files) {
        ProductVariant variant = productRepository.findAll().stream()
                .flatMap(p -> p.getVariants().stream())
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Variant not found"));

        if (files != null && !files.isEmpty()) {
            List<ProductImage> newImages = files.stream()
                    .map(file -> {
                        try {
                            ProductImage img = new ProductImage();
                            img.setData(file.getBytes());
                            img.setVariant(variant);
                            return img;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            if (variant.getImages() != null)
                variant.getImages().addAll(newImages);
            else
                variant.setImages(newImages);
        }
    }

    @Transactional
    public void deleteVariant(Integer productId,Integer variantId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.getVariants().removeIf(v -> v.getId().equals(variantId));
    }

    @Transactional(readOnly = true)
    public byte[] getProductImage(Integer productId, Integer variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Variante spécifique
        ProductVariant variant;
        if (variantId != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException("Variant not found"));
        } else {
            // Si pas de variantId fourni, prend la première variante disponible
            if (product.getVariants() == null || product.getVariants().isEmpty()) {
                return null;
            }
            variant = product.getVariants().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No variants found"));


        }

        if (variant.getImages() != null && !variant.getImages().isEmpty()) {
            return variant.getImages().get(0).getData(); // renvoie la première image
        }

        return null;
    }

    @Transactional(readOnly = true)
    public byte[] getProductImageByIndex(Integer productId, Integer variantId, int index) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ProductVariant variant;
        if (variantId != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException("Variant not found"));
        } else {
            if (product.getVariants() == null || product.getVariants().isEmpty()) {
                return null;
            }
            variant = product.getVariants().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No variants found"));

        }

        if (variant.getImages() != null && index >= 0 && index < variant.getImages().size()) {
            return variant.getImages().get(index).getData();
        }

        return null;
    }

    @Transactional
    public void deleteVariantImageByIndex(Integer variantId, int index) {
        ProductVariant variant = productRepository.findAll().stream()
                .flatMap(p -> p.getVariants().stream())
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Variant not found"));

        List<ProductImage> images = variant.getImages();
        if (images == null || index < 0 || index >= images.size()) {
            throw new IndexOutOfBoundsException("Invalid image index");
        }

        ProductImage removed = images.remove(index);
        removed.setVariant(null); // détache de la variante
    }

    @Transactional(readOnly = true)
    public byte[] getVariantImageByIndex(Integer variantId, int index) {
        // On cherche la variante parmi tous les produits
        ProductVariant variant = productRepository.findAll().stream()
                .flatMap(p -> p.getVariants().stream())
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Variant not found"));

        List<ProductImage> images = variant.getImages();
        if (images == null || index < 0 || index >= images.size()) {
            return null;
        }

        return images.get(index).getData();
    }

    @Transactional(readOnly = true)
    public List<ProductVariantDto> getAllVariantsByProductDto(Integer productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));

        return product.getVariants().stream().map(variant -> {
            ProductVariantDto dto = new ProductVariantDto();
            dto.setId(variant.getId());
            dto.setColor(variant.getColor());
            dto.setPrice(variant.getPrice());
            dto.setSizeClothing(variant.getSizeClothings());
            dto.setSizePants(variant.getSizePants());

            // on peut générer des URLs pour accéder aux images via un autre endpoint
            dto.setImageUrls(
                    variant.getImages() != null ?
                            variant.getImages().stream()
                                    .map(img -> "/products/" + productId + "/variants/" + variant.getId() + "/images/" + variant.getImages().indexOf(img))
                                    .toList() : List.of()
            );
            return dto;
        }).toList();
    }

    @Transactional
    public ProductVariantDto getVariantById(Integer productId,Integer variantId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id : "+productId));

        ProductVariant variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Variant with id " + variantId + " not found"));

        ProductVariantDto dto = new ProductVariantDto();
        dto.setId(variant.getId());
        dto.setColor(variant.getColor());
        dto.setPrice(variant.getPrice());
        dto.setSizeClothing(variant.getSizeClothings());
        dto.setSizePants(variant.getSizePants());
        dto.setImageUrls(
                variant.getImages() !=null
                ? variant.getImages().stream()
                        .map(img -> "/products/" + productId + "/variants/" + variant.getId() + "/images/" + variant.getImages().indexOf(img))
                        .toList()
                        : List.of()
        );
        return dto;
    }
}
