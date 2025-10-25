import { createContext, useContext, useEffect, useState } from 'react'
import { getCartItems } from '../Api'
import { useAuth } from './AuthContext'  // <- importer le AuthContext

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const { user } = useAuth(); 
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const refreshCart = async () => {
    if (!user?.userId) {
      setCartItems([])
      setLoading(false)
      return; 
    }
    try {
      setLoading(true);
      const res = await getCartItems(user.userId); 
      setCartItems(res.data || []);
    } catch (error) {
      console.error("Erreur lors du chargement du panier :", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshCart();
  }, [user]); 

  return (
    <CartContext.Provider value={{ cartItems, loading, refreshCart }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
