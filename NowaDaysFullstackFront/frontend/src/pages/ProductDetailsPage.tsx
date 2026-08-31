import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import type { Product } from '../types';
import { useCart } from '../context/CartContext';
import { FiArrowLeft, FiShoppingBag } from 'react-icons/fi';

export const ProductDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  
  const navigate = useNavigate();
  const { addToCart } = useCart();

  useEffect(() => {
    api.get<Product>(`/products/${id}`)
      .then(res => setProduct(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="p-8 text-center text-slate-500">Ładowanie szczegółów...</div>;
  if (!product) return <div className="p-8 text-center text-slate-500">Nie znaleziono produktu.</div>;

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <button 
        onClick={() => navigate('/shop')} 
        className="flex items-center gap-2 text-slate-600 hover:text-indigo-600 mb-6 font-medium text-sm"
      >
        <FiArrowLeft /> Powrót do sklepu
      </button>

      <div className="bg-white border border-slate-200 rounded-2xl p-8 shadow-sm grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 mb-2">{product.name}</h1>
          {product.category && (
            <span className="inline-block bg-indigo-50 text-indigo-700 font-semibold px-3 py-1 rounded-full text-xs mb-4">
              {product.category}
            </span>
          )}
          <p className="text-slate-600 leading-relaxed mb-6">
            {product.description || 'Brak opisu dla tego produktu.'}
          </p>
        </div>

        <div className="flex flex-col justify-between bg-slate-50 p-6 rounded-xl border border-slate-100">
          <div>
            <span className="text-sm text-slate-500 block mb-1">Cena jednostkowa</span>
            <div className="text-3xl font-extrabold text-indigo-600 mb-4">
              {product.price.toFixed(2)} PLN
            </div>
            <p className="text-sm text-slate-600 mb-6">Dostępność: <strong>{product.stockQuantity} szt.</strong></p>

            <div className="flex items-center gap-4 mb-6">
              <label className="text-sm font-medium text-slate-700">Ilość:</label>
              <input
                type="number"
                min="1"
                max={product.stockQuantity}
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                className="w-20 px-3 py-2 border border-slate-300 rounded-lg text-center font-bold"
              />
            </div>
          </div>

          <button
            onClick={() => {
              addToCart(product, quantity);
              alert('Dodano produkt do koszyka!');
            }}
            className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition flex items-center justify-center gap-2"
          >
            <FiShoppingBag /> Dodaj do koszyka
          </button>
        </div>
      </div>
    </div>
  );
};