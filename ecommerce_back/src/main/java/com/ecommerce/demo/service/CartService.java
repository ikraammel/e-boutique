package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.CartItemDto;
import com.ecommerce.demo.mappers.CartItemMapper;
import com.ecommerce.demo.models.Cart;
import com.ecommerce.demo.models.CartItem;
import com.ecommerce.demo.models.ProductVariant;
import com.ecommerce.demo.models.User;
import com.ecommerce.demo.repositories.CartRepository;
import com.ecommerce.demo.repositories.ProductVariantRepository;
import com.ecommerce.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public List<CartItem> getAllItems(Integer userId) {
        return getCartByUser(userId).getItems();
    }

    @Override
    @Transactional
    public CartItem addVariantToCart(Integer userId, Integer variantId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variantId)).findFirst()
                .orElse(null);

        CartItem item;
        if (existingItem != null){
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            item = existingItem;
        }else{
            item = new CartItem();
            item.setCart(cart);
            item.setQuantity(quantity);
            item.setVariant(productVariant);
            item.setSubtotal(productVariant.getPrice() * quantity);
            cart.getItems().add(item);
        }

        item.setSubtotal(productVariant.getPrice() * item.getQuantity());
        cart.calculateTotal();

        Cart savedCart = cartRepository.save(cart);
        return savedCart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variantId))
                .findFirst()
                .orElse(item);
    }

    @Override
    public void removeVariantFromCart(Integer userId, Integer variantId) {
        Cart cart = getCartByUser(userId);
        cart.getItems().removeIf(i -> i.getVariant().getId().equals(variantId));
        cart.calculateTotal();
        cartRepository.save(cart);
    }

    @Override
    public CartItem updateVariantQuantity(Integer userId, Integer variantId, int quantity) {
        Cart cart = getCartByUser(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Article non trouvé dans le panier"));

        item.setQuantity(quantity);
        cart.calculateTotal();
        cartRepository.save(cart);
        return item;
    }


    @Override
    public Cart getCartByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Panier introuvable"));
    }

    @Override
    public void clearCart(Integer userId) {
        Cart cart = getCartByUser(userId);
        cart.getItems().clear();
        cart.calculateTotal();
        cartRepository.save(cart);
    }

    @Override
    public CartItem updateCartItemVariant(Integer userId, Integer oldVariantId, Integer newVariantId) {
        Cart cart = getCartByUser(userId);

        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(oldVariantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Article non trouvé dans le panier"));

        ProductVariant newVariant = productVariantRepository.findById(newVariantId)
                .orElseThrow(() -> new RuntimeException("Nouvelle variante introuvable"));

        CartItem duplicate = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(newVariantId))
                .findFirst()
                .orElse(null);

        if (duplicate != null) {
            // Fusionne les quantités si la nouvelle variante existe déjà
            duplicate.setQuantity(duplicate.getQuantity() + existingItem.getQuantity());
            cart.getItems().remove(existingItem);
        } else {
            // Sinon on remplace la variante
            existingItem.setVariant(newVariant);
        }

        // Recalcul des sous-totaux et du total général
        cart.getItems().forEach(CartItem::calculateSubtotal);
        cart.calculateTotal();

        // ✅ Important : sauvegarde du panier pour persister le changement
        Cart savedCart = cartRepository.save(cart);

        return duplicate != null
                ? duplicate
                : savedCart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(newVariantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erreur lors de la mise à jour de la variante"));
    }


    @Transactional
    public List<CartItemDto> getAllItemsDto(Integer userId){
        return getAllItems(userId).stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    public CartItemDto toDto(CartItem item){
        return cartItemMapper.toDto(item);
    }

}
