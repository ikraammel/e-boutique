import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { addToCart, fetchProductById } from '../Api';
import './ProductDetail.css';
import SizeGuideModal from './SizeGuideModal';
import AddVariant from '../admin/ModificationVariante/AddVariant';
import UpdateVariant from '../admin/ModificationVariante/UpdateVariant';
import { toast, ToastContainer } from 'react-toastify';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';

function ProductDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showSizeGuide, setShowSizeGuide] = useState(false);
  const [quantity, setQuantity] = useState(1);

  const { user } = useAuth();
  const { refreshCart } = useCart();

  // Modals et sélection de variante
  const [showAddVariantModal, setShowAddVariantModal] = useState(false);
  const [showUpdateVariantModal, setShowUpdateVariantModal] = useState(false);
  const [selectedVariantId, setSelectedVariantId] = useState(null);
  const [isFading, setIsFading] = useState(false);

  // Mapping des couleurs CSS
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
      Kaki: '#78866b'
    };
    return map[colorName] || 'transparent';
  };

  // --- Fetch produit ---
  const fetchProduct = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const res = await fetchProductById(id);
      setProduct(res.data);

      if (res.data.variants?.[0]) {
        const firstVariantId = res.data.variants[0].id;
        setSelectedVariantId(firstVariantId);
        await handleColorClick(firstVariantId, res.data);
      }
    } catch (err) {
      console.error(err);
      setError("Impossible de charger le produit");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProduct();
  }, [id]);

  const selectedVariant = product?.variants?.find(v => v.id === selectedVariantId);

  // --- Gestion des images / couleurs ---
  const handleColorClick = async (variantId, currentProduct = product) => {
    if (variantId === selectedVariantId) return;
    setIsFading(true);
    setSelectedVariantId(variantId);

    const variant = currentProduct.variants.find(v => v.id === variantId);
    if (!variant || !variant.imageUrls) {
      setImages([]);
      setIsFading(false);
      return;
    }

    const urls = await Promise.all(
      variant.imageUrls.map(async (_, index) => {
        try {
          const imgRes = await axios.get(
            `http://localhost:8080/products/variants/${variant.id}/images/${index}`,
            { responseType: 'arraybuffer' }
          );
          return URL.createObjectURL(new Blob([imgRes.data], { type: 'image/jpeg' }));
        } catch (err) {
          if (err.response?.status === 404) {
            console.warn(`Image ${index} introuvable pour la variante ${variant.id}`);
            return null;
          }
          throw err;
        }
      })
    );

    setTimeout(() => {
      setImages(urls.filter(Boolean));
      setIsFading(false);
    }, 200);
  };

  // --- Supprimer une variante ---
  const handleDeleteVariant = async (variantId) => {
    if (!variantId) return;
    if (!window.confirm('Voulez-vous vraiment supprimer cette variante ?')) return;

    try {
      await axios.delete(`http://localhost:8080/products/${product.id}/variants/${variantId}`);
      const updatedVariants = product.variants.filter(v => v.id !== variantId);
      setProduct(prev => ({ ...prev, variants: updatedVariants }));

      if (selectedVariantId === variantId && updatedVariants.length > 0) {
        handleColorClick(updatedVariants[0].id);
      } else if (updatedVariants.length === 0) {
        setSelectedVariantId(null);
        setImages([]);
      }

      toast.success("Variante supprimée avec succès !");
    } catch (err) {
      console.error(err);
      toast.error("Erreur lors de la suppression de la variante");
    }
  };

  // --- Gestion quantité ---
  const handleQuantity = (newQuantity) => {
    if (newQuantity < 1) return;
    setQuantity(newQuantity);
  };

  // --- Ajouter au panier ---
  const handleAddToCart = async () => {
    if (!user) {
      toast.error("Veuillez vous connecter pour ajouter au panier !");
      return;
    }

    try {
      await addToCart(user.userId, selectedVariantId, quantity);
      toast.success("Produit ajouté au panier !");
      refreshCart();
    } catch (err) {
      console.error(err);
      toast.error("Erreur lors de l'ajout au panier");
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!product) return <div>Produit introuvable</div>;

  return (
    <div className='page'>
      <h2>{product.name}</h2>

      {/* Images */}
      <div className={`product-images ${isFading ? 'fading' : ''}`}>
        {images.length > 0 ? (
          images.map((url, index) => (
            <div key={index} className='image-container'>
              <img src={url} alt={`${product.name} ${index + 1}`} />
              <button
                className='delete-image-btn'
                onClick={async () => {
                  if (!window.confirm("Supprimer cette image ?")) return;
                  try {
                    await axios.delete(`http://localhost:8080/products/variants/${selectedVariantId}/images/${index}`);
                    handleColorClick(selectedVariantId);
                  } catch (err) {
                    console.error("Erreur lors de la suppression de l'image", err);
                    toast.error("Erreur lors de la suppression de l'image");
                  }
                }}
              >
                ✕
              </button>
            </div>
          ))
        ) : (
          <div className='placeholder'>Aucune image.</div>
        )}
      </div>

      {/* Palette de couleurs */}
      {product.variants?.length > 0 && (
        <div className='color-palette-wrapper'>
          <div className='color-palette'>
            {product.variants.map(variant => {
              const colorStyle = variant.colorCode || getCssColor(variant.color);
              const isSelected = selectedVariantId === variant.id;

              return (
                <div key={variant.id} className='variant-btn-wrapper'>
                  <button
                    className={`color-btn ${isSelected ? 'selected' : ''}`}
                    style={{ background: colorStyle }}
                    onClick={() => handleColorClick(variant.id)}
                    title={variant.color}
                  />
                  {user?.role === 'ADMIN' && (
                    <div className='variant-actions'>
                      <button
                        className='edit-variant-btn'
                        onClick={(e) => { e.stopPropagation(); setSelectedVariantId(variant.id); setShowUpdateVariantModal(true); }}
                        title="Modifier la variante"
                      >
                        ✎
                      </button>
                      <button
                        className='delete-variant-btn'
                        onClick={(e) => { e.stopPropagation(); handleDeleteVariant(variant.id); }}
                        title="Supprimer la variante"
                      >
                        ×
                      </button>
                    </div>
                  )}

                </div>
              );
            })}
          </div>
        </div>
      )}

      <p>{product.description}</p>
      <p>Prix: {selectedVariant?.price} MAD</p>

      {/* Quantité */}
      <div className="quantity-wrapper">
        <button disabled={quantity === 1} onClick={() => handleQuantity(quantity - 1)}>-</button>
        <span>{quantity}</span>
        <button onClick={() => handleQuantity(quantity + 1)}>+</button>
      </div>

      <p>Couleur: {selectedVariant?.color}</p>

      <button className='btn-add-to-cart' onClick={handleAddToCart}>
        🛒 Ajouter au panier
      </button>

      {/* Ajouter variante */}
      {user && user.role === 'ADMIN' && (
        <button className="btn-secondary" onClick={() => setShowAddVariantModal(true)}>
        Ajouter une variante
      </button>

      )}
      
      {showAddVariantModal && (
        <AddVariant
          productId={product.id}
          onClose={() => setShowAddVariantModal(false)}
          onVariantAdded={fetchProduct}
        />
      )}

      {showUpdateVariantModal && selectedVariantId && (
        <UpdateVariant
          productId={product.id}
          variantId={selectedVariantId}
          onClose={() => setShowUpdateVariantModal(false)}
          onVariantUpdated={fetchProduct}
        />
      )}

      {/* Guide des tailles */}
      <button className="size-guide-btn" onClick={() => setShowSizeGuide(true)}>
        Guide des tailles
      </button>

      {showSizeGuide && <SizeGuideModal onClose={() => setShowSizeGuide(false)} />}

      {/* Tailles */}
      {selectedVariant?.sizeClothing?.length > 0 && (
        <div className="product-sizes">
          {selectedVariant.sizeClothing.map(size => <button key={size} className="size-btn">{size}</button>)}
        </div>
      )}
      {selectedVariant?.sizePants?.length > 0 && (
        <div className='product-sizes'>
          {selectedVariant.sizePants.map(size => <button key={size} className='size-btn'>{size}</button>)}
        </div>
      )}

      <ToastContainer/>
    </div>
  );
}

export default ProductDetail;
