import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { addToCart, updateQuantity } from '../Api';
import './ProductCard.css';
import { toast, ToastContainer } from 'react-toastify';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';

function ProductCard({ product }) {
  const [selectedVariant, setSelectedVariant] = useState(
    product?.variants?.[0] || null
  );
  const { user } = useAuth();
  const { refreshCart } = useCart(); 
  const userId = user?.userId;
  const [quantity,setQuantity] = useState(1);

  const handleVariantChange = variant => {
    setSelectedVariant(variant)
  }

  const imageUrl =
      selectedVariant?.imageUrls?.length > 0
        ? `http://localhost:8080${selectedVariant.imageUrls[0]}`
        : '/placeholder.jpg';

  const handleAddToCart = async (e) => {
  e.preventDefault();

  if (!userId) {
    toast.error("Veuillez vous connecter pour ajouter un produit au panier !");
    return;
  }

  if (!selectedVariant) {
    toast.error("Aucune variante sélectionnée !");
    return;
  }

  try {
    console.log("USER CONTEXT:", user);

    await addToCart(userId, selectedVariant.id, quantity);
    toast.success("Produit ajouté au panier !");
    refreshCart()
  } catch (err) {
    console.error(err);
    toast.error("Erreur lors de l’ajout au panier.");
  }
};


  const handleQuantity = async(newQuantity) => {
      if(newQuantity < 1) return ;
      setQuantity(newQuantity)
  }

  function getCssColor(colorName) {
    const map = {
      Rouge: 'red',
      Bleu: 'blue',
      'Bleu marine': 'navy',
      Noir: 'black',
      Blanc: 'white',
      Vert: 'green',
      'Vert olive': 'olive',
      Jaune: 'yellow',
      Rose: 'pink',
      Violet: 'purple',
      Gris: 'gray',
      Kaki: '#78866b',
    };
    return map[colorName] || '#ccc';
  }

  return (
    <div className="card compact">
      <Link to={`/products/${product.id}`} className="card-link">
        <div className="card-image">
          <img src={imageUrl} alt={product.name} />
        </div>
        <div className="card-body">
          <h3 className="card-title">{product?.name || 'Produit'}</h3>
          <p className="card-price">
            {selectedVariant?.price || product.price || 'N/A'} MAD
          </p>
        </div>
      </Link>

      {product.variants?.length > 0 && (
        <div className="variant-colors">
          {product.variants.map((variant) => (
            <button
              key={variant.id}
              className={`color-dot ${
                selectedVariant.id === variant.id ? 'selected' : ''
              }`}
              title={variant.color}
              style={{ backgroundColor: getCssColor(variant.color) }}
              onClick={() => handleVariantChange(variant)}
            ></button>
          ))}
        </div>
      )}

      <button className="btn-add-cart black" onClick={handleAddToCart}>
        🛒 Ajouter au panier
      </button>
      <div className="quantity-wrapper">
          <button disabled={quantity === 1} onClick={() => handleQuantity(quantity - 1)}>-</button>
          <span>{quantity}</span>
          <button onClick={() => handleQuantity(quantity + 1)}>+</button>
      </div>
    </div>
  );
}

export default ProductCard;
