import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiShoppingCart, FiUser, FiLogOut, FiCreditCard, FiMail } from 'react-icons/fi';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <nav className="bg-slate-900 text-white p-4 shadow-md">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="text-xl font-bold tracking-wide text-indigo-400">
          NowadaysShop
        </Link>

     
        <div className="flex items-center gap-6">
          <Link to="/" className="hover:text-indigo-300">Sklep</Link>
          <Link to="/contact" className="hover:text-indigo-300 flex items-center gap-1">
            <FiMail /> Kontakt
          </Link>
          
          {user ? (
            <>
              <Link to="/history" className="hover:text-indigo-300 flex items-center gap-1">
                <FiShoppingCart /> Zamówienia
              </Link>
              <Link 
                to="/profile" 
                className="flex items-center gap-2 bg-slate-800 px-3 py-1.5 rounded-lg border border-slate-700 hover:border-indigo-500"
              >
                <span className="text-green-400 flex items-center gap-1">
                  <FiCreditCard />
                  <span className="font-semibold">{user.balance.toFixed(2)} PLN</span>
                </span>
                <span className="text-slate-400">|</span>
                <span className="flex items-center gap-1">
                  <FiUser /> {user.username}
                </span>
              </Link>
              <button 
                onClick={() => { logout(); navigate('/'); }}
                className="text-red-400 hover:text-red-300 flex items-center gap-1"
              >
                <FiLogOut /> Wyloguj
              </button>
            </>
          ) : (
            
            <div className="flex items-center gap-6">
              <Link to="/login" className="hover:text-indigo-300 leading-none">
                Zaloguj się
              </Link>
              <Link to="/register" className="hover:text-indigo-300 leading-none">
                Zarejestruj się
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};