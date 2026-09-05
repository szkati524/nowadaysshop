import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { FiPlusCircle, FiAlertTriangle, FiArrowLeft } from 'react-icons/fi';

interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  initialStock: number;
  category: string;
}

export const AddProduct: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<CreateProductRequest>({
    name: '',
    description: '',
    price: 0,
    initialStock: 1,
    category: '',
  });

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);


  const isAdmin = user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN' || user?.role?.includes('ADMIN');

  if (!user || !isAdmin) {
    return (
      <div className="container mx-auto p-6 text-center text-white">
        <div className="bg-slate-800 p-8 rounded-xl max-w-md mx-auto border border-rose-500/40 shadow-2xl">
          <FiAlertTriangle className="text-rose-500 text-5xl mx-auto mb-4" />
          <h2 className="text-2xl font-bold mb-2">Brak Dostępu</h2>
          <p className="text-slate-400 mb-6">
            Ta sekcja wymaga uprawnień administratora.
          </p>
          <button
            onClick={() => navigate('/')}
            className="bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-5 py-2.5 rounded-lg transition cursor-pointer"
          >
            Wróć do sklepu
          </button>
        </div>
      </div>
    );
  }

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'price' || name === 'initialStock' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {

      await api.post('/products', formData);
      navigate('/');
    } catch (err: any) {
      console.error('Błąd podczas dodawania produktu:', err);
      const serverMessage = err.response?.data?.message || err.response?.data?.error;
      setError(serverMessage || 'Wystąpił błąd podczas dodawania produktu. Sprawdź poprawność danych.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6 max-w-2xl">
      <button
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-slate-400 hover:text-white mb-6 transition cursor-pointer"
      >
        <FiArrowLeft /> Powrót do katalogu
      </button>

      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-8 text-white shadow-2xl">
        <h1 className="text-2xl font-bold mb-6 flex items-center gap-2 text-indigo-400">
          <FiPlusCircle /> Dodaj Nowy Produkt
        </h1>

        {error && (
          <div className="bg-rose-500/10 border border-rose-500/50 text-rose-400 p-4 rounded-xl mb-6 text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium mb-1.5 text-slate-300">
              Nazwa produktu *
            </label>
            <input
              type="text"
              name="name"
              required
              value={formData.name}
              onChange={handleChange}
              placeholder="np. Klawiatura Mechaniczna"
              className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5 text-slate-300">
              Kategoria *
            </label>
            <input
              type="text"
              name="category"
              required
              value={formData.category}
              onChange={handleChange}
              placeholder="np. Elektronika"
              className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1.5 text-slate-300">
              Opis
            </label>
            <textarea
              name="description"
              rows={3}
              value={formData.description}
              onChange={handleChange}
              placeholder="Szczegółowy opis produktu..."
              className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white focus:outline-none focus:border-indigo-500 transition resize-none"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1.5 text-slate-300">
                Cena (PLN) *
              </label>
              <input
                type="number"
                step="0.01"
                min="0"
                name="price"
                required
                value={formData.price}
                onChange={handleChange}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white focus:outline-none focus:border-indigo-500 transition"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1.5 text-slate-300">
                Stan początkowy (initialStock) *
              </label>
              <input
                type="number"
                min="0"
                name="initialStock"
                required
                value={formData.initialStock}
                onChange={handleChange}
                className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-white focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-6 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-3.5 rounded-xl transition disabled:opacity-50 cursor-pointer flex justify-center items-center gap-2"
          >
            {loading ? 'Zapisywanie...' : 'Dodaj produkt'}
          </button>
        </form>
      </div>
    </div>
  );
};