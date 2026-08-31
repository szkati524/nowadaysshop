import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import type { Product, PagedResult } from '../types';
import { useNavigate } from 'react-router-dom';
import { FiSearch, FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { useCart } from '../context/CartContext';

const CATEGORIES = ['ELECTRONICS', 'BOOKS', 'CLOTHING', 'HOME', 'SPORT'];

export const ShopPage: React.FC = () => {
  const [pagedResult, setPagedResult] = useState<PagedResult<Product> | null>(null);
  const [loading, setLoading] = useState(true);
  

  const [searchName, setSearchName] = useState('');
  const [category, setCategory] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [page, setPage] = useState(0);

  const navigate = useNavigate();
  const { addToCart } = useCart();

  const fetchProducts = () => {
    setLoading(true);
    const params: Record<string, any> = { page, size: 6 };
    if (searchName) params.name = searchName;
    if (category) params.category = category;
    if (minPrice) params.minPrice = parseFloat(minPrice);
    if (maxPrice) params.maxPrice = parseFloat(maxPrice);

    api.get<PagedResult<Product>>('/products/search', { params })
      .then(res => setPagedResult(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchProducts();
  }, [page, category]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    fetchProducts();
  };

  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <h1 className="text-2xl font-bold mb-6 text-slate-800">Katalog Produktów</h1>

    
      <form onSubmit={handleSearchSubmit} className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm mb-8 space-y-4 md:space-y-0 md:flex md:items-center md:gap-4 flex-wrap">
        <div className="relative flex-1 min-w-[200px]">
          <input
            type="text"
            placeholder="Szukaj po nazwie..."
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="w-full pl-4 pr-10 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none text-sm"
          />
          <button type="submit" className="absolute right-2 top-2.5 text-slate-400 hover:text-indigo-600">
            <FiSearch className="w-5 h-5" />
          </button>
        </div>

        <select
          value={category}
          onChange={(e) => { setCategory(e.target.value); setPage(0); }}
          className="border border-slate-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
        >
          <option value="">Wszystkie kategorie</option>
          {CATEGORIES.map(cat => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>

        <div className="flex items-center gap-2">
          <input
            type="number"
            placeholder="Cena od"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            className="w-24 px-3 py-2 border border-slate-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <span className="text-slate-400">-</span>
          <input
            type="number"
            placeholder="Cena do"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            className="w-24 px-3 py-2 border border-slate-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <button
          type="submit"
          className="bg-indigo-600 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition"
        >
          Filtruj
        </button>
      </form>


      {loading ? (
        <div className="p-8 text-center text-slate-500">Ładowanie produktów...</div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            {pagedResult?.content.map(product => (
              <div 
                key={product.id} 
                onClick={() => navigate(`/products/${product.id}`)}
                className="border rounded-lg p-5 shadow-sm bg-white flex flex-col justify-between cursor-pointer hover:shadow-md transition border-slate-200"
              >
                <div>
                  <h2 className="text-xl font-semibold mb-1 text-slate-900 hover:text-indigo-600">
                    {product.name}
                  </h2>
                  {product.category && (
                    <span className="inline-block text-xs bg-indigo-50 text-indigo-700 font-semibold px-2 py-0.5 rounded mb-3">
                      {product.category}
                    </span>
                  )}
                  <p className="text-gray-600 mb-4 text-sm">W magazynie: {product.stockQuantity} szt.</p>
                </div>
                
                <div className="flex justify-between items-end mt-4 pt-3 border-t border-slate-100">
                  <span className="text-lg font-bold text-indigo-600">{product.price.toFixed(2)} PLN</span>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      addToCart(product);
                    }}
                    className="bg-indigo-600 text-white px-4 py-2 rounded text-sm font-medium hover:bg-indigo-700 transition"
                  >
                    Do koszyka
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Kontrolki Paginacji */}
          {pagedResult && pagedResult.totalPages > 1 && (
            <div className="flex justify-center items-center gap-4 mt-6">
              <button
                disabled={page === 0}
                onClick={() => setPage(prev => prev - 1)}
                className="p-2 border rounded-lg disabled:opacity-50 hover:bg-slate-50"
              >
                <FiChevronLeft className="w-5 h-5" />
              </button>
              <span className="text-sm font-medium text-slate-600">
                Strona {page + 1} z {pagedResult.totalPages}
              </span>
              <button
                disabled={page + 1 >= pagedResult.totalPages}
                onClick={() => setPage(prev => prev + 1)}
                className="p-2 border rounded-lg disabled:opacity-50 hover:bg-slate-50"
              >
                <FiChevronRight className="w-5 h-5" />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};