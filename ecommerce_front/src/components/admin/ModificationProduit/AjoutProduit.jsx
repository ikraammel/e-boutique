import React, { useEffect, useState } from 'react';
import { addProduct,fetchAllCategories } from '../../Api';

function AjoutProduit() {
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        price: 0,
        stock: 0,
        files: [],
        color: "",
        sizeClothing: [],
        sizePants: []
    });

    const [categories,setCategories] = useState([])

    useEffect(() => {
        const loadCategories = async () => {
            try{
                const data = await fetchAllCategories()
                  setCategories(data)
            } catch(err){
                console.error("Erreur lors du chargement des catégories", err)
            }
        }
        loadCategories()
    },[])

    // Gestion des champs texte
    const handleChange = e => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // Gestion des fichiers
    const handleFileChange = e => {
        setFormData(prev => ({ ...prev, files: Array.from(e.target.files) }));
    };

    // Tailles vêtements
    const handleSizeClothingChange = e => {
        const values = Array.from(e.target.selectedOptions, option => option.value);
        setFormData(prev => ({
            ...prev,
            sizeClothing: values,
            sizePants: []
        }));
    };

    // Tailles pantalons
    const handleSizePantsChange = e => {
        const values = Array.from(e.target.selectedOptions, option => option.value);
        setFormData(prev => ({
            ...prev,
            sizePants: values,
            sizeClothing: []
        }));
    };

    // Soumission du formulaire
    const handleSubmit = async e => {
        e.preventDefault();
        try {
            await addProduct(formData);
            alert("Produit ajouté avec succès !")
            setFormData({
                name: "",
                description: "",
                price: 0,
                stock: 0,
                files: [],
                category: "",
                color: "",
                sizeClothing: [],
                sizePants: []
            });
        } catch (err) {
            console.error(err.response?.data || err);
            alert("Erreur lors de l'ajout du produit");
        }
    };

    return (
        <div className="container mt-5">
            <h2 className="mb-4">Ajouter un produit</h2>
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

                <div className="col-12">
                    <label className="form-label">Images</label>
                    <input type="file" name="files" multiple onChange={handleFileChange} className="form-control" />
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

                <div className="col-md-6">
                    <label className="form-label">Couleur</label>
                    <input type="text" name="color" value={formData.color} onChange={handleChange} className="form-control" required />
                </div>

                <div className="col-md-6">
                    <label className="form-label">Tailles Vêtements</label>
                    <select multiple value={formData.sizeClothing} onChange={handleSizeClothingChange} className="form-select">
                        {["XS", "S", "M", "L", "XL", "XXL", "XXXL"].map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                </div>

                <div className="col-md-6">
                    <label className="form-label">Tailles Pantalons</label>
                    <select multiple value={formData.sizePants} onChange={handleSizePantsChange} className="form-select">
                        {["36", "38", "40", "42", "44", "46"].map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                </div>

                <div className="col-12">
                    <button type="submit" className="btn btn-primary mt-3">Ajouter</button>
                </div>

            </form>
        </div>
    );
}

export default AjoutProduit;
