import React, { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useCart } from "../contexts/CartContext";
import { useAuth } from "../contexts/AuthContext";
import "./Navbar.css";

function Navbar() {
  const [search, setSearch] = useState("");
  const { cartItems, loading, refreshCart } = useCart();
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    const q = search.trim();
    navigate(q ? `/products?search=${encodeURIComponent(q)}` : "/products");
  };

  const handleLogout = () => {
    logout();
    refreshCart(); // vider le panier si déconnexion
    navigate("/"); // rediriger vers accueil
  };

  const totalItems = cartItems.reduce((acc, item) => acc + (item.quantity || 0), 0);

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light shadow-sm">
      <div className="container">
        <Link to="/" className="navbar-brand fw-bold">
          EBoutique
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          aria-controls="navbarContent"
          aria-expanded="false"
          aria-label="Basculer la navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarContent">
  {/* Zone gauche - vide ou logo secondaire */}
  <div className="navbar-left"></div>

  {/* Zone centrale - Recherche */}
  <div className="search-container">
    <input
      id="navSearch"
      type="search"
      className="search-input"
      placeholder="Rechercher un produit ..."
      aria-label="Rechercher"
      value={search}
      onChange={(e) => setSearch(e.target.value)}
    />
    <button className="btn-search" type="button" onClick={handleSubmit} aria-label="Rechercher">
      <i className="bi bi-search"></i>
    </button>
  </div>

  {/* Zone droite - Navigation */}
  <ul className="navbar-nav navbar-right mb-2 mb-lg-0 align-items-lg-center">
    {user && (
      <li className="nav-item">
        <span className="nav-link">👋 Bonjour {user.firstName || ""}</span>
      </li>
    )}
    <li className="nav-item dropdown">
      <NavLink
        to="/products"
        className={({ isActive }) => `nav-link dropdown-toggle${isActive ? " active" : ""}`}
        id="navbarDropdown"
        role="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
      >
        Produits
      </NavLink>
      <ul className="dropdown-menu" aria-labelledby="navbarDropdown">
        <li>
          <NavLink className="dropdown-item" to="/products/category/WOMEN">
            Femmes
          </NavLink>
        </li>
        <li>
          <NavLink className="dropdown-item" to="/products/category/MEN">
            Hommes
          </NavLink>
        </li>
        <li>
          <NavLink className="dropdown-item" to="/products/category/KIDS">
            Bébé
          </NavLink>
        </li>
        <li>
          <NavLink className="dropdown-item" to="/products/category/ACCESSORIES">
            Accessoires
          </NavLink>
        </li>
      </ul>
    </li>

    {/* Panier */}
    <li className="nav-item cart">
      <NavLink
        to="/cart"
        className={({ isActive }) => `nav-link position-relative${isActive ? " active" : ""}`}
      >
        Panier
        <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
          {loading ? "..." : totalItems > 0 ? totalItems : 0}
          <span className="visually-hidden">articles dans le panier</span>
        </span>
      </NavLink>
    </li>

    {/* Connexion / Déconnexion */}
    {user ? (
      <li className="nav-item">
        <span className="nav-link" style={{ cursor: 'pointer' }} onClick={handleLogout}>
          Déconnexion
        </span>
      </li>
    ) : (
      <>
        <li className="nav-item login">
          <NavLink
            to="/login"
            className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}
          >
            Se connecter
          </NavLink>
        </li>
        <li className="nav-item signin">
          <NavLink
            to="/registrate"
            className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}
          >
            S'inscrire
          </NavLink>
        </li>
      </>
    )}
  </ul>
</div>
      </div>
    </nav>
  );
}

export default Navbar;
