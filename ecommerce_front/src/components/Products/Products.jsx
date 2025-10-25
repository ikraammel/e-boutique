import React, { useEffect, useState } from 'react'
import { deleteProduct, fetchProducts } from '../Api'
import ProductCard from './ProductCard'
import { useLocation, useNavigate } from 'react-router-dom'
import { fetchProductsByName, deleteVariant } from '../Api'
import AddVariant from '../admin/ModificationVariante/AddVariant'
import { Modal } from 'react-bootstrap'
import './Products.css'
import { useAuth } from '../contexts/AuthContext'

function Products() {
  const [products,setProducts] = useState([])
  const [loading,setLoading] = useState(false)
  const [error,setError] = useState(null)
  const [showVariantModal, setShowVariantModal] = useState(false);
  const [currentProductId, setCurrentProductId] = useState(null);
  const navigate = useNavigate()
  const {user} = useAuth()

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search)
  const search = searchParams.get('search') || '';

  const loadProducts = async () => {
    setLoading(true)
    try {
      const res = search ? await fetchProductsByName(search) : await fetchProducts()
      setProducts(res.data)
    } catch(err) {
      setError('Erreur lors du chargement')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProducts()
  }, [search])

  const handleDeleteVariant = async (productId, variantId) => {
    if(!window.confirm('Voulez-vous vraiment supprimer cette variante ?')) return;
    try {
      await deleteVariant(productId, variantId)
      // Rafraîchir l'état local
      setProducts(prev => prev.map(p => {
        if(p.id !== productId) return p;
        return {...p, variants: p.variants.filter(v => v.id !== variantId)}
      }))
      alert("Variante supprimée !")
    } catch(err) {
      console.error(err)
      alert("Erreur lors de la suppression de la variante")
    }
  }
  const handleDeleteProduct = async (id) => {
      if(!window.confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) return;
      try {
        await deleteProduct(id);
        setProducts(prev => prev.filter(p => p.id !== id));
        alert("Produit supprimé avec succès !");
      } catch(err) {
        console.error(err);
      }
    };

    const handleEditProduct = (id) => {
    navigate(`/admin/modifier-produit/${id}`);
  };

  const handleOpenVariantModal = (productId) => {
    setCurrentProductId(productId);
    setShowVariantModal(true);
  };

  const handleCloseVariantModal = () => {
    setShowVariantModal(false);
    setCurrentProductId(null);
  };

  if(loading) return <div className='center'>Chargement ...</div>
  if(error) return <div className='center error'>{error}</div>

  return (
    <div className='page'>
      <section className="products-section">
        <h2>Produits</h2>
        <div className="grid">
          {products.length ? (
            products.map(p => (
              <div key={p.id} className="product-card-wrapper">
                <ProductCard
                  product={p}
                  onDeleteVariant={handleDeleteVariant}
                />
                {user && user.role === 'ADMIN' && (
                  <div className="admin-buttons">
                  <button onClick={() => handleEditProduct(p.id)}>Modifier</button>
                  <button onClick={() => handleDeleteProduct(p.id)}>Supprimer</button>
                  <button onClick={() => handleOpenVariantModal(p.id)}>Ajouter variante</button>
                </div>
                  )}
              </div>
            ))
          ) : (
            <p>Aucun produit trouvé.</p>
          )}
        </div>
      </section> 
      {user && user.role === 'ADMIN' && (
      <Modal show={showVariantModal} onHide={handleCloseVariantModal}>
        <Modal.Header closeButton>
          <Modal.Title>Ajouter une variante</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {currentProductId && (
            <AddVariant 
              productId={currentProductId} 
              onClose={handleCloseVariantModal} 
              onVariantAdded={loadProducts}
            />
          )}
        </Modal.Body>
      </Modal>
      )}
    </div>
  )
}

export default Products
