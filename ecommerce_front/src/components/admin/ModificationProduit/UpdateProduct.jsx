import React, { useEffect, useState } from 'react'
import { fetchAllCategories, fetchProductById, updateProduct } from '../../Api'
import { useParams } from 'react-router-dom'

function UpdateProduct() {

    const {id} = useParams()
    const[formData,setFormData] = useState({
        name:"",
        description:"",
        price: 0,
        stock: 0
    })

    const[categories,setCategories] = useState([])

    useEffect(() => {
        const loadProduct = async() => {
            try{
                const res = await fetchProductById(id)
                const data = res.data
                setFormData({
                    name:data.name,
                    description: data.description,
                    price: data.price,
                    stock: data.stock,
                    category: data.category
                })
            }catch(err){
                console.error("Erreur lors du chargement du produit", err)
            }
        }
        loadProduct()
    },[id])

    useEffect(() => {
        const loadCategories = async() => {
            try{
                const data = await fetchAllCategories()
                setCategories(data)
            } catch(err){
                console.error("Erreur lors du chargement des catégories", err)
            }
        }
        loadCategories() 
    },[])

    const handleChange = e => {
        const {name,value} = e.target
        setFormData(prev => ({ ...prev, [name]:value}))
    }

    const handleSubmit = async(e) => {
        e.preventDefault()
        try{
            await updateProduct(id,formData)
            alert("Produit modifié avec succès !")
            setFormData({
                name:"",
                description: "",
                price:0,
                stock:0
            })
        }catch (err) {
            console.error(err.response?.data || err);
            alert("Erreur lors de la modification du produit");
        }
    }

  return (
    <div className='container mt-5'>
      <h2 className="mb-4">Modifier un produit</h2>
      <form onSubmit={handleSubmit} className="row g-3">
        <div className="col-12">
                    <label className="form-label">Nom</label>
                    <input type="text" name="name" value={formData.name} onChange={handleChange} className="form-control" required />
                </div>

                <div className="col-12">
                    <label className="form-label">Description</label>
                    <input type="text" name="description" value={formData.description} onChange={handleChange} className="form-control" required />
                </div>

                <div className="col-md-6">
                    <label className="form-label">Stock</label>
                    <input type="number" name="stock" value={formData.stock} onChange={handleChange} className="form-control" required />
                </div>

                <div className="col-md-6">
                    <label className="form-label">Prix</label>
                    <input type="number" name="price" value={formData.price} onChange={handleChange} className="form-control" required />
                </div>

                <div className="col-md-6">
                    <label className="form-label">Catégorie</label>
                    <select name="category" value={formData.category} onChange={handleChange} className='form-select'>
                    <option value="">--Choisir une catégorie --</option>
                    {categories.map(c => (
                        <option key={c} value={c}>{c}</option>
                    ))}
                    </select>
                </div>

                <div className="col-12">
                    <button type="submit" className="btn btn-primary mt-3">Modifier</button>
                </div>

      </form>
    </div>
  )
}

export default UpdateProduct
