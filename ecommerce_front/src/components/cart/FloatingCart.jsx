import React, { useEffect, useState } from 'react';
import { getCartItems } from '../Api';
import { useNavigate } from 'react-router-dom';
import './FloatingCart.css';
import { useAuth } from '../contexts/AuthContext';

function FloatingCart() {
  const navigate = useNavigate();
  const {user} = useAuth()

  if(!user) return null;
  
  return (
    <div className="floating-cart">
      <button onClick={() => navigate('/cart')}>Voir le panier</button>
    </div>
  );
}

export default FloatingCart;
