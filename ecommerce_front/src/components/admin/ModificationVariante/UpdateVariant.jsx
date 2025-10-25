import React, { useEffect, useState } from 'react';
import './UpdateVariant.css';
import {
  addVariantImages,
  deleteVariant,
  deleteVariantImage,
  getVariantById,
  updateVariant
} from '../../Api';

function UpdateVariant({ productId, variantId, onClose, onVariantUpdated }) {
  const [formData, setFormData] = useState({
    color: "",
    sizeClothings: [],
    sizePants: [],
    files: [],
    price: 0
  });

  const [variantImages, setVariantImages] = useState([]);
  const BASE_URL = "http://localhost:8080";

  // Charger les images de la variante
  const fetchVariantImages = async () => {
    try {
      const res = await getVariantById(productId, variantId);
      setVariantImages(res.data.imageUrls || []); 
    } catch (err) {
      console.error("Erreur lors du chargement des images", err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    setFormData((prev) => ({ ...prev, files: Array.from(e.target.files) }));
  };

  const handleSizeClothingChange = (e) => {
    const values = Array.from(e.target.selectedOptions, (option) => option.value);
    setFormData((prev) => ({ ...prev, sizeClothings: values, sizePants: [] }));
  };

  const handleSizePantsChange = (e) => {
    const values = Array.from(e.target.selectedOptions, (option) => option.value);
    setFormData((prev) => ({ ...prev, sizePants: values, sizeClothings: [] }));
  };

  const handleDeleteVariant = async () => {
    if (window.confirm("Supprimer cette variante ?")) {
      try {
        await deleteVariant(productId, variantId);
        alert("Variante supprimée avec succès !");
        onVariantUpdated?.();
        onClose?.();
      } catch (err) {
        console.error("Erreur lors de la suppression de la variante", err);
        alert("Erreur lors de la suppression de la variante");
      }
    }
  };

  useEffect(() => {
    const loadVariant = async () => {
      try {
        const res = await getVariantById(productId, variantId);
        const v = res.data;
        setFormData({
          color: v.color || "",
          sizeClothings: v.sizeClothing || [],
          sizePants: v.sizePants || [],
          files: [],
          price: v.price || 0
        });
        setVariantImages(v.imageUrls || []); 
      } catch (err) {
        console.error("Erreur lors du chargement de la variante", err);
      }
    };
    if (productId && variantId) loadVariant();
  }, [productId, variantId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const form = new FormData();
      form.append("color", formData.color);
      formData.sizeClothings.forEach((size) => form.append("sizeClothings", size));
      formData.sizePants.forEach((size) => form.append("sizePants", size));
      form.append("price",formData.price)

      await updateVariant(productId, variantId, form);

      // 2️⃣ Upload des fichiers si présents
      if (formData.files.length > 0) {
        const imgData = new FormData();
        formData.files.forEach((file) => imgData.append("files", file));
        await addVariantImages(variantId, imgData);
      }

      alert("Variante mise à jour avec succès !");
      onVariantUpdated?.();
      onClose?.();
    } catch (err) {
      console.error("Erreur lors de la mise à jour de la variante", err);
      alert("Erreur lors de la mise à jour de la variante");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="edit-variant-form">
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
      <input type="number" name='price' value={formData.price} onChange={handleChange} step="0.01" min="0" required/>

      <label>Tailles Vêtements</label>
      <select multiple value={formData.sizeClothings} onChange={handleSizeClothingChange}>
        {["XS", "S", "M", "L", "XL", "XXL"].map((size) => (
          <option value={size} key={size}>{size}</option>
        ))}
      </select>

      <label>Tailles Pantalons</label>
      <select multiple value={formData.sizePants} onChange={handleSizePantsChange}>
        {["36", "38", "40", "42", "44", "46"].map((size) => (
          <option value={size} key={size}>{size}</option>
        ))}
      </select>

      <button type="submit" className="btn btn-primary mt-3">Modifier</button>
      <button type="button" onClick={handleDeleteVariant} className="btn btn-danger mt-3">
        Supprimer cette variante
      </button>
    </form>
  );
}

export default UpdateVariant;
