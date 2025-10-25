import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  getCartItems, removeVariantFromCart, clearCart, 
  updateQuantity, changeVariant 
} from '../Api';
import './Cart.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useAuth } from '../contexts/AuthContext';

function Cart() {
  const navigate = useNavigate(); 
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const { user } = useAuth();

  const getCssColor = (colorName) => {
    const map = { 
      Rouge: 'red', Bleu: 'blue', 'Bleu marine': 'navy', Noir: 'black', 
      Blanc: 'white', Vert: 'green', 'Vert olive': 'olive', Jaune: 'yellow', 
      Rose: 'pink', Violet: 'purple', Gris: 'gray', Kaki: '#78866b' 
    };
    return map[colorName] || 'transparent';
  };

  const fetchCart = async () => {
  const userId = user?.userId;
  if (!userId) return;

  setIsLoading(true);
  try {
    const res = await getCartItems(userId);
    setItems(res.data || []);
    const totalAmount = (res.data || []).reduce((sum, item) => sum + item.subtotal, 0);
    setTotal(totalAmount);
  } catch (err) {
    console.error(err);
    // Ici tu ne touches pas à "error", sinon tu affiches deux fois
    toast.error("Impossible de charger le panier."); 
  } finally {
    setIsLoading(false);
  }
};


  const handleDelete = async (variantId) => {
    const userId = user?.userId;
    if (!userId) return;

    try {
      const confirmDelete = window.confirm("Etes-vous sûr de la suppression de cet article ?");
      if (!confirmDelete) return;

      await removeVariantFromCart(variantId, userId);
      const updatedItems = items.filter(item => item.variantId !== variantId);
      setItems(updatedItems);
      const totalAmount = updatedItems.reduce((sum, item) => sum + item.subtotal, 0);
      setTotal(totalAmount);
      toast.success("Produit supprimé avec succès !");
    } catch (err) {
      console.error(err);
      toast.error("Impossible de supprimer ce produit.");
    }
  };

  const handleClearCart = async () => {
    const userId = user?.userId;
    if (!userId) return;

    const confirmDelete = window.confirm("Voulez-vous vraiment vider le panier ?");
    if (!confirmDelete) return;

    try {
      await clearCart(userId);
      setItems([]);
      setTotal(0);
      toast.success("Panier vidé avec succès !");
    } catch (err) {
      console.error(err);
      toast.error("Impossible de vider le panier.");
    }
  };

  const handleQuantity = async (item, newQuantity) => {
    const userId = user?.userId;
    if (!userId || newQuantity < 1) return;

    try {
      await updateQuantity(userId, item.variantId, newQuantity);
      const updatedItems = items.map(i =>
        i.variantId === item.variantId
          ? { ...i, quantity: newQuantity, subtotal: newQuantity * i.price }
          : i
      );
      setItems(updatedItems);
      const totalAmount = updatedItems.reduce((sum, i) => sum + i.subtotal, 0);
      setTotal(totalAmount);
    } catch (err) {
      console.error(err);
      toast.error("Impossible de modifier la quantité.");
    }
  };

  const handleChangeVariant = async (item, newVariantId) => {
    const userId = user?.userId;
    if (!userId || newVariantId === item.variantId) return;

    try {
      await changeVariant(userId, item.variantId, newVariantId);
      const updatedItems = items.map(i =>
        i.variantId === item.variantId
          ? { ...i, variantId: newVariantId }
          : i
      );
      setItems(updatedItems);
      toast.success("Couleur mise à jour !");
      fetchCart();
    } catch (err) {
      console.error(err);
      toast.error("Impossible de changer la couleur.");
    }
  };

  useEffect(() => {
    if (user === null) return; // on attend que AuthProvider initialise le user

    if (!user) {
      toast.error("Veuillez vous connecter afin d'accéder à votre panier.");
      navigate("/login");
    } else {
      fetchCart();
    }
  }, [user]);

  return (
    <div className="cart-container">
      {isLoading && <p>Chargement...</p>}
      {error && <p>{error}</p>}

      {items.length === 0 ? (
        <p>Votre panier est vide.</p>
      ) : (
        items.map(item => (
          <div key={item.id} className="cart-item">
            <img
              src={`http://localhost:8080/products/variants/${item.variantId}/images/0`}
              alt={item.productName}
              onClick={() => navigate(`/products/${item.productId}`)}
              style={{ cursor: 'pointer' }}
            />
            <div className="cart-item-details">
              <p
                onClick={() => navigate(`/products/${item.productId}`)}
                style={{ cursor: 'pointer', fontWeight: 'bold' }}
              >
                {item.productName}
              </p>

              <p className="cart-quantity">
                Quantité:
                <button disabled={item.quantity === 1} onClick={() => handleQuantity(item, item.quantity - 1)}>-</button>
                <span>{item.quantity}</span>
                <button onClick={() => handleQuantity(item, item.quantity + 1)}>+</button>
              </p>
              <b>{item.subtotal} DH</b>

              <div className="color-palette">
                Couleur:
                {item.availableVariants?.map(v => {
                  const colorStyle = getCssColor(v.color);
                  const isSelected = item.variantId === v.id;
                  return (
                    <button
                      key={v.id}
                      className={`color-btn ${isSelected ? 'selected' : ''}`}
                      style={{ background: colorStyle }}
                      onClick={() => handleChangeVariant(item, v.id)}
                      title={v.color}
                    />
                  );
                })}
              </div>

              <button className="btn-delete" onClick={() => handleDelete(item.variantId)}>
                Supprimer
              </button>
            </div>
          </div>
        ))
      )}

      <div className="cart-total">Total: {total} DH</div>
      <div className="btn-clear" onClick={handleClearCart}>Vider le panier</div>
      <ToastContainer />
    </div>
  );
}

export default Cart;
