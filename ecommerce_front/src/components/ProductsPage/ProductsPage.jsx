import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { addToCart, fetchProductByCategory } from '../Api';
import './ProductsPage.css';
import { toast, ToastContainer } from 'react-toastify';
import { useAuth } from '../contexts/AuthContext';

function ProductsPage() {
  const { category } = useParams();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedVariants, setSelectedVariants] = useState({});
  const [quantities, setQuantities] = useState({}); // Quantité par produit
  const { user } = useAuth();
  const userId = user?.userId;


  // mapping des couleurs en CSS
  const getCssColor = (colorName) => {
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
  };

  useEffect(() => {
    if (category) {
      setLoading(true);
      fetchProductByCategory(category)
        .then(res => setProducts(res.data))
        .catch(err => console.error(err))
        .finally(() => setLoading(false));
    }
  }, [category]);

  // Modifier la quantité pour un produit spécifique
  const handleQuantity = (productId, newQuantity) => {
    if (newQuantity < 1) return;
    setQuantities(prev => ({
      ...prev,
      [productId]: newQuantity
    }));
  };

  if (loading) return <div className="center">Chargement...</div>;

  return (
    <div className="products-container">
      {products.length > 0 ? (
        <div className="products-grid">
          {products.map(product => {
            const firstVariant =
              product.variants && product.variants.length > 0
                ? product.variants[0]
                : null;

            const selectedVariantId = selectedVariants[product.id] || firstVariant?.id;
            const selectedVariant = product.variants?.find(v => v.id === selectedVariantId) || firstVariant;

            const imageUrl =
              selectedVariant && selectedVariant.imageUrls && selectedVariant.imageUrls.length > 0
                ? `http://localhost:8080${selectedVariant.imageUrls[0]}`
                : "/placeholder.jpg";

            const productQuantity = quantities[product.id] || 1; // Quantité par produit

            return (
              <div key={product.id} className="product-card">
                <Link to={`/products/${product.id}`} className="product-link">
                  <div className="product-image">
                    <img src={imageUrl} alt={product.name || "Produit"} />
                  </div>
                  <div className="product-infos">
                    <h3 className="product-title">{product.name}</h3>
                    <p className="product-desc">{product.description}</p>
                  </div>
                </Link>

                {/* Palette de couleurs */}
                {product.variants?.length > 0 && (
                  <div className="variant-colors">
                    {product.variants.map(variant => (
                      <button
                        key={variant.id}
                        className={`color-dot ${selectedVariantId === variant.id ? 'selected' : ''}`}
                        style={{ backgroundColor: getCssColor(variant.color) }}
                        title={variant.color}
                        onClick={() =>
                          setSelectedVariants(prev => ({
                            ...prev,
                            [product.id]: variant.id
                          }))
                        }
                      ></button>
                    ))}
                  </div>
                )}

                <div className="product-footer">
                  <span className="product-price">{selectedVariant?.price || product.price} MAD</span>

                  <div className="product-actions">
                    {/* Quantité par produit */}
                    <div className="quantity-wrapper">
                      <button
                        disabled={productQuantity === 1}
                        onClick={() => handleQuantity(product.id, productQuantity - 1)}
                      >
                        -
                      </button>
                      <span>{productQuantity}</span>
                      <button
                        onClick={() => handleQuantity(product.id, productQuantity + 1)}
                      >
                        +
                      </button>
                    </div>

                    {/* Bouton Ajouter au panier */}
                    <button
                      className="btn-add-to-cart"
                      onClick={() => {
                        if (!selectedVariant) return alert("Aucune variante disponible");
                        addToCart(userId, selectedVariant.id, productQuantity)
                          .then(() => toast.success("Produit ajouté au panier !"))
                          .catch(() => toast.error("Erreur lors de l'ajout"));
                      }}
                    >
                      🛒 Ajouter au panier
                    </button>
                  </div>
                </div>

              </div>
            );
          })}
        </div>
      ) : (
        <p className="no-products">
          Aucun produit trouvé pour la catégorie <b>{category}</b>.
        </p>
      )}
      <ToastContainer/>
    </div>
  );
}

export default ProductsPage;
