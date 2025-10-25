import React, { useEffect, useState } from 'react';
import { fetchProducts } from '../Api';
import ProductCard from '../Products/ProductCard';
import './Home.css'
import ImageSlider from '../ImageSlider/ImageSlider';
import { ToastContainer } from 'react-toastify';

function Home() {
  const [products, setProducts] = useState([]);
  const [sliderImages, setSliderImages] = useState([])

  useEffect(() => {
    fetchProducts()
      .then(res => {
        const allProducts = res.data 

        // On prend seulement 6 produits pour la homepage
        const featuredProducts = allProducts.slice(0, 10);
        setProducts(featuredProducts);

        const images = featuredProducts
            .map(p => p.image)
            .filter(Boolean)
            setSliderImages(images)
      })
      .catch(err => console.error(err));
  }, []);

  return (
    <div>
      {/* Slider avec les images des produits */}
      {sliderImages.length > 0 && <ImageSlider images={sliderImages} />}

      {/* Produits en vedette */}
      <section className="featured-products">
        <h2>Nos Produits en Vedette</h2>
        <div className="grid">
          {products.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>
      <ToastContainer/>
    </div>
  );
}


export default Home
