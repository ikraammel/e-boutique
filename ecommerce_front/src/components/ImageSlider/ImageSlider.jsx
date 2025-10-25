import React, { useState } from 'react';
import './ImageSlider.css';

function ImageSlider({ images }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  const prevSlide = () => {
    setCurrentIndex(prev => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const nextSlide = () => {
    setCurrentIndex(prev => (prev === images.length - 1 ? 0 : prev + 1));
  };

  if (!images || images.length === 0) return null;

  return (
    <div className="slider-horizontal">
      <button className="slider-btn left" onClick={prevSlide}>&lt;</button>
      <div className="slider-track">
        <img
          src={images[currentIndex]}
          alt={`Slide ${currentIndex + 1}`}
          className="slider-image"
        />
      </div>
      <button className="slider-btn right" onClick={nextSlide}>&gt;</button>
    </div>
  );
}

export default ImageSlider;
