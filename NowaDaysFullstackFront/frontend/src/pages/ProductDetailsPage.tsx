import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import type { Product } from '../types';
import { useAuth } from '../context/AuthContext';
import { 
  FiArrowLeft, FiShoppingCart, FiTruck, 
  FiShield, FiRefreshCw, FiCheckCircle 
} from 'react-icons/fi';

export const ProductDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, refreshBalance } = useAuth();

  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [purchasing, setPurchasing] = useState(false);

  useEffect(() => {
    if (!id) return;
    api.get<Product>(`/products/${id}`)
      .then((res) => setProduct(res.data))
      .catch((err) => console.error('Błąd pobierania szczegółów produktu:', err))
      .finally(() => setLoading(false));
  }, [id]);

  const handleBuy = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (!product) return;

    setPurchasing(true);
    try {
      await api.post('/orders', {
        userId: user.id,
        productId: product.id,
        quantity: quantity,
      });
      alert(`Zamówienie złożone pomyślnie! Kupiono ${quantity} szt.`);
      await refreshBalance();
      
      setProduct({ ...product, stockQuantity: product.stockQuantity - quantity });
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas składania zamówienia');
    } finally {
      setPurchasing(false);
    }
  };

  if (loading) {
    return <div className="p-12 text-center text-slate-500">Ładowanie szczegółów produktu...</div>;
  }

  if (!product) {
    return (
      <div className="container mx-auto p-8 text-center">
        <h2 className="text-xl font-bold text-slate-800">Nie znaleziono produktu</h2>
        <Link to="/" className="text-indigo-600 hover:underline mt-4 inline-block">
          Powrót do sklepu
        </Link>
      </div>
    );
  }

  const totalPrice = product.price * quantity;

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
   
      <button
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-slate-600 hover:text-indigo-600 mb-6 transition"
      >
        <FiArrowLeft /> Powrót do listy produktów
      </button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 bg-white p-8 rounded-2xl border border-slate-200 shadow-sm">
        
      
        <div className="bg-slate-100 rounded-xl flex items-center justify-center p-12 border border-slate-200 min-h-[320px]">
          <span className="text-slate-400 font-semibold text-lg">{product.name}</span>
        </div>

     
        <div className="flex flex-col justify-between space-y-6">
          <div>
            <span className="text-xs font-semibold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2.5 py-1 rounded-full">
              W magazynie: {product.stockQuantity} szt.
            </span>
            <h1 className="text-3xl font-bold text-slate-900 mt-3 mb-2">{product.name}</h1>
            <p className="text-2xl font-bold text-indigo-600 mb-4">{product.price.toFixed(2)} PLN / szt.</p>
            
            <p className="text-slate-600 text-sm leading-relaxed mb-6">
              Oryginalny produkt najwyższej jakości oferowany przez NowadaysShop. Wykonany z dbałością o każdy detal, z pełnym wsparciem gwarancyjnym i natychmiastową wysyłką z naszego magazynu.
            </p>

          
            <div className="flex items-center gap-4 mb-6">
              <label className="text-sm font-semibold text-slate-700">Ilość sztuk:</label>
              <div className="flex items-center border border-slate-300 rounded-lg overflow-hidden">
                <button
                  type="button"
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold"
                >
                  -
                </button>
                <span className="px-4 py-1.5 text-slate-900 font-semibold">{quantity}</span>
                <button
                  type="button"
                  onClick={() => setQuantity(Math.min(product.stockQuantity, quantity + 1))}
                  className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold"
                >
                  +
                </button>
              </div>
            </div>
          </div>

        
          <div className="border-t border-slate-100 pt-4 space-y-4">
            <div className="flex justify-between items-center text-slate-800">
              <span className="font-semibold">Suma całkowita:</span>
              <span className="text-2xl font-bold text-slate-900">{totalPrice.toFixed(2)} PLN</span>
            </div>

            <button
              onClick={handleBuy}
              disabled={purchasing || product.stockQuantity < 1}
              className="w-full bg-indigo-600 text-white py-3 rounded-xl font-medium hover:bg-indigo-700 transition disabled:opacity-50 flex items-center justify-center gap-2 text-lg shadow-sm"
            >
              <FiShoppingCart />
              {user ? 'Kup teraz' : 'Zaloguj się, aby kupić'}
            </button>
          </div>

      
          <div className="grid grid-cols-3 gap-2 border-t border-slate-100 pt-4 text-xs text-slate-500">
            <div className="flex items-center gap-1.5">
              <FiTruck className="text-indigo-600 shrink-0" /> Wysyłka 24h
            </div>
            <div className="flex items-center gap-1.5">
              <FiShield className="text-indigo-600 shrink-0" /> Gwarancja 2 lata
            </div>
            <div className="flex items-center gap-1.5">
              <FiRefreshCw className="text-indigo-600 shrink-0" /> 14 dni na zwrot
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};