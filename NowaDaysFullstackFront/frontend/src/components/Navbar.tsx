import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { FiShoppingCart, FiUser, FiLogOut, FiCreditCard, FiMail } from 'react-icons/fi';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  
  const totalCartItems = cart.reduce((sum, item) => sum + item.quantity, 0);

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
              <Link to="/order-history" className="hover:text-indigo-300 flex items-center gap-1 relative">
                <FiShoppingCart /> 
                <span>Zamówienia</span>
                {totalCartItems > 0 && (
                  <span className="bg-indigo-500 text-white text-xs font-bold px-2 py-0.5 rounded-full ml-1">
                    {totalCartItems}
                  </span>
                )}
              </Link>

              <Link 
                to="/profile" 
                className="flex items-center gap-2 bg-slate-800 px-3 py-1.5 rounded-lg border border-slate-700 hover:border-indigo-500 transition"
              >
                <span className="text-emerald-400 flex items-center gap-1">
                  <FiCreditCard />
                  <span className="font-semibold">
                    {(user.balance ?? 0).toFixed(2)} PLN
                  </span>
                </span>
                <span className="text-slate-400">|</span>
                <span className="flex items-center gap-1">
                  <FiUser /> {user.username || user.email}
                </span>
              </Link>

              <button 
                onClick={() => { logout(); navigate('/'); }}
                className="text-rose-400 hover:text-rose-300 flex items-center gap-1 transition cursor-pointer"
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