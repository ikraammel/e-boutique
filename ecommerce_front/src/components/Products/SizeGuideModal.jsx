import React from 'react';
import ReactDOM from 'react-dom';
import './SizeGuideModal.css';

export default function SizeGuideModal({ onClose }) {
  return ReactDOM.createPortal(
    <div className="size-guide-modal">
      <div className="modal-content">
        <h3>Guide des tailles</h3>
        <p>Exemple pour les pulls:</p>
        <ul>
          <li>S : poitrine 80-84 cm</li>
          <li>M : poitrine 85-89 cm</li>
          <li>L : poitrine 90-94 cm</li>
          <li>XL : poitrine 95-99 cm</li>
        </ul>
        <button onClick={onClose}>Fermer</button>
      </div>
    </div>,
    document.getElementById('modal-root')
  );
}
