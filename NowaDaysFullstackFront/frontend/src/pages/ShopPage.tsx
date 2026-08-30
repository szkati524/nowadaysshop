import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import type { Product } from '../types';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export const ShopPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const { user, refreshBalance } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    api.get<Product[]>('/products')
      .then(res => setProducts(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const handleBuy = async (productId: string, e: React.MouseEvent) => {
    e.stopPropagation(); 
    if (!user) {
      navigate('/login');
      return;
    }
    try {
      await api.post('/orders', {
        userId: user.id,
        productId: productId,
        quantity: 1
      });
      alert('Zamówienie złożone pomyślnie!');
      await refreshBalance();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas składania zamówienia');
    }
  };

  if (loading) return <div className="p-8 text-center text-slate-500">Ładowanie produktów...</div>;

  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <h1 className="text-2xl font-bold mb-6 text-slate-800">Dostępne Produkty</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {products.map(p => (
          <div 
            key={p.id} 
            onClick={() => navigate(`/products/${p.id}`)}
            className="border rounded-lg p-5 shadow-sm bg-white flex flex-col justify-between cursor-pointer hover:shadow-md transition border-slate-200"
          >
            <div>
              <Link to={`/products/${p.id}`} className="text-xl font-semibold mb-2 text-slate-900 hover:text-indigo-600 block">
                {p.name}
              </Link>
              <p className="text-gray-600 mb-4 text-sm">W magazynie: {p.stockQuantity} szt.</p>
            </div>
            
            <div className="flex justify-between items-end mt-4 pt-3 border-t border-slate-100 min-h-[52px]">
              <span className="text-lg font-bold text-indigo-600 pb-1">{p.price.toFixed(2)} PLN</span>
              <button
                onClick={(e) => handleBuy(p.id, e)}
                className="bg-indigo-600 text-white px-4 py-2 rounded text-sm font-medium hover:bg-indigo-700 transition leading-tight h-10 flex items-center justify-center"
              >
                {user ? 'Kup teraz' : 'Zaloguj się, aby kupić'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};