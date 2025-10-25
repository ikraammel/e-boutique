import React from 'react'
import './App.css'
import { BrowserRouter,Routes,Route } from 'react-router-dom'
import Navbar from './components/Navbar/Navbar'
import Home from './components/HomePage/Home'
import Products from './components/Products/Products'
import ProductDetail from './components/Products/ProductDetail'
import ProductsPage from './components/ProductsPage/ProductsPage'
import AjoutProduit from './components/admin/ModificationProduit/AjoutProduit'
import UpdateProduct from './components/admin/ModificationProduit/UpdateProduct'
import AddVariant from './components/admin/ModificationVariante/AddVariant'
import UpdateVariant from './components/admin/ModificationVariante/UpdateVariant'
import Cart from './components/cart/Cart'
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import FloatingCart from './components/cart/FloatingCart'
import Login from './components/auth/login/Login'
import Registrate from './components/auth/registrate/Registrate'
import LoginSuccess from './components/auth/login/LoginSuccess'
import Dashboard from './components/admin/Dashboard'

function App() {
  return (
    <>
      <BrowserRouter>
        <Navbar/>
        <Routes>
          <Route path='/' element={<Home/>}/>
          <Route path='/products' element={<Products/>}/>
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/products/category/:category" element={<ProductsPage/>} />
          <Route path="/admin/ajout-produit" element={<AjoutProduit/>}></Route>
          <Route path="/admin/modifier-produit/:id" element={<UpdateProduct/>}></Route>
          <Route path="/admin/add-variant" element={<AddVariant/>}></Route>
          <Route path="/admin/edit-variant" element={<UpdateVariant/>}></Route>
          <Route path="/cart" element={<Cart/>}></Route>
          <Route path="/login" element={<Login/>}></Route>
          <Route path="/registrate" element={<Registrate/>}></Route>
          <Route path="/login/success" element={<LoginSuccess />} />
          <Route path="/admin/dashboard" element={<Dashboard/>} />
          
        </Routes>
        <FloatingCart/>
        <ToastContainer 
            position="top-right"
            autoClose={5000}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="colored"
            closeButton={true}
          />
      </BrowserRouter>
    </>
  )
}

export default App
