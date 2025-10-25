package com.ecommerce.demo.controller;

import com.ecommerce.demo.dtos.ProductDto;
import com.ecommerce.demo.dtos.ProductVariantDto;
import com.ecommerce.demo.mappers.ProductMapper;
import com.ecommerce.demo.models.*;
import com.ecommerce.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer productId) {
        ProductDto dto = productService.getProductById(productId);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam(required = false, defaultValue = "0") int stock,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) Category category,
            @RequestParam String color,
            @RequestParam(required = false) List<SizeClothing> sizeClothing,
            @RequestParam(required = false) List<SizePants> sizePants
    ) {
        ProductDto product = productService.createProduct(name, description, price, stock, files, category, color, sizeClothing, sizePants);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Integer productId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Category category
    ) {
        ProductDto updated = productService.updateProduct(productId, name, description, price, stock, category);
        return ResponseEntity.ok(updated);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam(required = false) String search) {
        if (search == null || search.trim().isEmpty()) {
            return ResponseEntity.ok(productService.getAllProducts());
        } else {
            var dtos = productService.searchProductsByName(search)
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
            return ResponseEntity.ok(dtos);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Category category) {
        List<ProductDto> dtos = productService.getProductsByCategory(category);
        return ResponseEntity.ok(dtos);
    }

    // ==================== IMAGES PRODUITS ====================

    @GetMapping("/{productId}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer productId) {
        byte[] image = productService.getProductImage(productId, null);
        if (image == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }

    @GetMapping("/{productId}/images/{index}")
    public ResponseEntity<byte[]> getProductImageByIndex(@PathVariable Integer productId, @PathVariable int index) {
        byte[] image = productService.getProductImageByIndex(productId, null, index);
        if (image == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}/images/{index}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Integer productId, @PathVariable int index) {
        try {
            productService.deleteProductImageByIndex(productId, null, index);
            return ResponseEntity.noContent().build();
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== VARIANTS ====================

    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> addVariant(
            @PathVariable Integer productId,
            @RequestParam String color,
            @RequestParam double price,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) List<String> sizeClothings,
            @RequestParam(required = false) List<String> sizePants
    ){
        // Convertir les strings en enums
        List<SizeClothing> clothings = sizeClothings != null ?
                sizeClothings.stream().map(SizeClothing::valueOfLabel).toList() : null;

        List<SizePants> pants = sizePants != null ?
                sizePants.stream().map(SizePants::valueOfLabel).toList() : null;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addVariant(productId, color, price,files, clothings, pants));
    }

    @PatchMapping(
            value = "/{productId}/{variantId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateVariant(
            @PathVariable Integer productId,
            @PathVariable Integer variantId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) double price,
            @RequestParam(required = false, name = "sizeClothings") List<String> sizeClothingsStr,
            @RequestParam(required = false, name = "sizePants") List<String> sizePantsStr
    ) {
        List<SizeClothing> sizeClothings = sizeClothingsStr != null
                ? sizeClothingsStr.stream().map(SizeClothing::valueOfLabel).toList()
                : null;

        List<SizePants> sizePants = sizePantsStr != null
                ? sizePantsStr.stream().map(SizePants::valueOfLabel).toList()
                : null;

        ProductDto dto = productService.updateVariant(productId, variantId,price, color, sizeClothings, sizePants);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(
            value = "/variants/{variantId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addVariantImages(
            @PathVariable Integer variantId,
            @RequestParam List<MultipartFile> files
    ) {
        productService.addVariantImages(variantId, files);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{productId}/variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVariant(@PathVariable Integer productId, @PathVariable Integer variantId) {
        productService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/variants/{variantId}/images/{index}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVariantImage(@PathVariable Integer variantId, @PathVariable int index) {
        try {
            productService.deleteVariantImageByIndex(variantId, index);
            return ResponseEntity.noContent().build();
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/variants/{variantId}/images/{index}")
    public ResponseEntity<byte[]> getVariantImageByIndex(@PathVariable Integer variantId, @PathVariable int index) {
        byte[] image = productService.getVariantImageByIndex(variantId, index);
        if (image == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }

    @GetMapping("/categories")
    public ResponseEntity<Category[]> getAllCategories(){
        return ResponseEntity.ok(Category.values());
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantDto>> getAllVariantsByProduct(@PathVariable Integer productId){
        List<ProductVariantDto> variants = productService.getAllVariantsByProductDto(productId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantDto> getVariantById(@PathVariable Integer productId,
                                                            @PathVariable Integer variantId){
        ProductVariantDto dto = productService.getVariantById(productId,variantId);
        return ResponseEntity.ok(dto);
    }
}
