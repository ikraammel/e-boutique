import React, { useState } from 'react';
import { addVariant } from '../../Api';
import './AddVariant.css';

function AddVariant({ productId, onClose, onVariantAdded }) {
  const [formData, setFormData] = useState({
    color: "",
    sizeClothing: [],
    sizePants: [],
    files: [],
    price: 0
  });

  // Gestion des champs texte
  const handleChange = e => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  // Gestion des fichiers
  const handleFileChange = e => {
    setFormData(prev => ({ ...prev, files: Array.from(e.target.files) }));
  };

  // Gestion des tailles vêtements
  const handleSizeClothingChange = e => {
    const values = Array.from(e.target.selectedOptions, option => option.value);
    setFormData(prev => ({ ...prev, sizeClothing: values, sizePants: [] }));
  };

  // Gestion des tailles pantalons
  const handleSizePantsChange = e => {
    const values = Array.from(e.target.selectedOptions, option => option.value);
    setFormData(prev => ({ ...prev, sizePants: values, sizeClothing: [] }));
  };

  const handleSubmit = async e => {
  e.preventDefault();
  try {
    const data = new FormData();
    data.append("color", formData.color);
    data.append("price",formData.price)

    // Envoyer uniquement le champ rempli
    if (formData.sizeClothing.length > 0) {
      formData.sizeClothing.forEach(size => data.append("sizeClothing", size));
    } else if (formData.sizePants.length > 0) {
      formData.sizePants.forEach(size => data.append("sizePants", size));
    } else {
      alert("Veuillez sélectionner au moins une taille !");
      return;
    }

    formData.files.forEach(file => data.append("files", file));

    await addVariant(productId, data);

    if (onVariantAdded) onVariantAdded();
    alert("Variante ajoutée avec succès !");
    onClose();
  } catch (err) {
    console.error("Erreur lors de l'ajout de la variante", err);
    alert("Erreur lors de l'ajout de la variante");
  }
};

  return (
    <form onSubmit={handleSubmit} className="add-variant-form">
      <label>Couleur</label>
      <input
        type="text"
        name="color"
        value={formData.color}
        onChange={handleChange}
        required
      />

      <label>Images</label>
      <input type="file" multiple onChange={handleFileChange} />

      <label>Prix</label>
      <input type="number" name="price" value={formData.price} min="0" step="0.01"  onChange={handleChange} required />

      <label>Tailles Vêtements</label>
      <select multiple value={formData.sizeClothing} onChange={handleSizeClothingChange}>
        {["XS","S","M","L","XL","XXL"].map(size => (
          <option value={size} key={size}>{size}</option>
        ))}
      </select>

      <label>Tailles Pantalons</label>
      <select multiple value={formData.sizePants} onChange={handleSizePantsChange}>
        {["36","38","40","42","44","46"].map(size => (
          <option value={size} key={size}>{size}</option>
        ))}
      </select>

      <button type="submit" className="btn btn-primary mt-3">Ajouter</button>
    </form>
  );
}

export default AddVariant;
