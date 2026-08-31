import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';
import { 
  FiUser, 
  FiCreditCard, 
  FiPlusCircle, 
  FiMinusCircle, 
  FiTrash2, 
  FiShoppingBag,
  FiPackage 
} from 'react-icons/fi';
import type { OrderHistoryItem } from '../types';

export const ProfilePage: React.FC = () => {
  const { user, refreshBalance } = useAuth();
  const { cart, removeFromCart, clearCart, totalAmount } = useCart();

  const [depositAmount, setDepositAmount] = useState<string>('50');
  const [withdrawAmount, setWithdrawAmount] = useState<string>('');
  const [iban, setIban] = useState<string>('');
  
  const [loadingDeposit, setLoadingDeposit] = useState(false);
  const [loadingWithdraw, setLoadingWithdraw] = useState(false);
  const [loadingCheckout, setLoadingCheckout] = useState(false);

  const [orders, setOrders] = useState<OrderHistoryItem[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(false);

  useEffect(() => {
    if (user) {
      fetchOrders();
    }
  }, [user]);

  const fetchOrders = async () => {
    if (!user) return;
    setLoadingOrders(true);
    try {
  
      const response = await api.get(`/orders/user/${user.id}`);
      setOrders(response.data);
    } catch (err) {
      console.error('Nie udało się pobrać historii zamówień', err);
    } finally {
      setLoadingOrders(false);
    }
  };

  if (!user) {
    return <div className="p-8 text-center text-slate-600">Musisz się zalogować.</div>;
  }

  const handleDeposit = async (e: React.FormEvent) => {
    e.preventDefault();
    const numericAmount = parseFloat(depositAmount);

    if (isNaN(numericAmount) || numericAmount <= 0) {
      alert('Wprowadź prawidłową kwotę większą od 0.');
      return;
    }

    setLoadingDeposit(true);
    try {
      await api.post(`/users/${user.id}/deposit`, { amount: numericAmount });
      await refreshBalance();
      alert(`Pomyślnie doładowano konto o ${numericAmount.toFixed(2)} PLN!`);
      setDepositAmount('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas doładowania salda.');
    } finally {
      setLoadingDeposit(false);
    }
  };

  const handleWithdraw = async (e: React.FormEvent) => {
    e.preventDefault();
    const numericAmount = parseFloat(withdrawAmount);

    if (isNaN(numericAmount) || numericAmount <= 0 || numericAmount > (user.balance ?? 0)) {
      alert('Nieprawidłowa kwota wypłaty.');
      return;
    }

    setLoadingWithdraw(true);
    try {
      await api.post(`/users/${user.id}/withdraw`, { amount: numericAmount });
      await refreshBalance();
      alert(`Pomyślnie zlecono wypłatę na konto ${iban}!`);
      setWithdrawAmount('');
      setIban('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas wypłaty.');
    } finally {
      setLoadingWithdraw(false);
    }
  };

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    if ((user.balance ?? 0) < totalAmount) {
      alert('Niewystarczające środki w portfelu na opłacenie całego koszyka!');
      return;
    }

    setLoadingCheckout(true);
    try {
      for (const item of cart) {
        
        await api.post('/orders', {
          productId: item.product.id,
          quantity: item.quantity
        });
      }

      await refreshBalance();
      clearCart();
      await fetchOrders();
      alert('Pomyślnie opłacono i złożono wszystkie zamówienia!');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas opłacania koszyka.');
    } finally {
      setLoadingCheckout(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-10 max-w-4xl space-y-8">

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">
        <div className="flex items-center gap-4 border-b border-slate-100 pb-6 mb-6">
          <div className="w-16 h-16 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center text-2xl font-bold">
            {user.firstName ? user.firstName[0] : <FiUser />}
          </div>
          <div>
            <h1 className="text-2xl font-bold text-slate-900">{user.firstName} {user.lastName}</h1>
            <p className="text-slate-500 text-sm">{user.email}</p>
          </div>
        </div>

        <div className="bg-slate-50 p-6 rounded-xl border border-slate-200 flex items-center justify-between">
          <div>
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Dostępne środki</span>
            <div className="text-3xl font-extrabold text-indigo-600 mt-1">
              {(user.balance ?? 0).toFixed(2)} PLN
            </div>
          </div>
          <FiCreditCard className="w-10 h-10 text-slate-300" />
        </div>
      </div>

      
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">
        <h2 className="text-xl font-bold text-slate-800 mb-6 flex items-center gap-2">
          <FiShoppingBag className="text-indigo-600" /> Twój Koszyk
        </h2>

        {cart.length === 0 ? (
          <p className="text-slate-500 text-sm">Twój koszyk jest pusty.</p>
        ) : (
          <div className="space-y-4">
            {cart.map(item => (
              <div key={item.product.id} className="flex items-center justify-between border-b border-slate-100 pb-4">
                <div>
                  <h4 className="font-semibold text-slate-900">{item.product.name}</h4>
                  <p className="text-xs text-slate-500">
                    {item.quantity} x {item.product.price.toFixed(2)} PLN
                  </p>
                </div>
                <div className="flex items-center gap-4">
                  <span className="font-bold text-slate-800">
                    {(item.product.price * item.quantity).toFixed(2)} PLN
                  </span>
                  <button
                    onClick={() => removeFromCart(item.product.id)}
                    className="text-rose-500 hover:text-rose-700"
                  >
                    <FiTrash2 />
                  </button>
                </div>
              </div>
            ))}

            <div className="pt-4 flex justify-between items-center text-lg font-bold text-slate-900">
              <span>Razem do zapłaty:</span>
              <span className="text-indigo-600">{totalAmount.toFixed(2)} PLN</span>
            </div>

            <button
              onClick={handleCheckout}
              disabled={loadingCheckout}
              className="w-full bg-emerald-600 text-white py-3 rounded-lg font-medium hover:bg-emerald-700 transition disabled:opacity-50 mt-4"
            >
              {loadingCheckout ? 'Przetwarzanie płatności...' : 'Zapłać za koszyk'}
            </button>
          </div>
        )}
      </div>

     
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <form onSubmit={handleDeposit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
            <FiPlusCircle className="text-emerald-600" /> Doładuj Portfel
          </h3>
          <div>
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={depositAmount}
              onChange={(e) => setDepositAmount(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
              required
            />
          </div>
          <button
            type="submit"
            disabled={loadingDeposit}
            className="w-full bg-emerald-600 text-white py-2 rounded-lg font-medium hover:bg-emerald-700 transition disabled:opacity-50"
          >
            {loadingDeposit ? 'Doładowywanie...' : 'Doładuj'}
          </button>
        </form>

        <form onSubmit={handleWithdraw} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
            <FiMinusCircle className="text-rose-600" /> Wypłać Środki
          </h3>
          <input
            type="text"
            placeholder="Numer IBAN"
            value={iban}
            onChange={(e) => setIban(e.target.value)}
            className="w-full px-4 py-2 border border-slate-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-indigo-500"
            required
          />
          <input
            type="number"
            min="0.01"
            step="0.01"
            placeholder="Kwota"
            value={withdrawAmount}
            onChange={(e) => setWithdrawAmount(e.target.value)}
            className="w-full px-4 py-2 border border-slate-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-indigo-500"
            required
          />
          <button
            type="submit"
            disabled={loadingWithdraw}
            className="w-full bg-slate-800 text-white py-2 rounded-lg font-medium hover:bg-slate-900 transition disabled:opacity-50"
          >
            {loadingWithdraw ? 'Wypłacanie...' : 'Wypłać'}
          </button>
        </form>
      </div>

     
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8">
        <h2 className="text-xl font-bold text-slate-800 mb-6 flex items-center gap-2">
          <FiPackage className="text-indigo-600" /> Historia Zamówień
        </h2>

        {loadingOrders ? (
          <p className="text-slate-500 text-sm">Ładowanie historii...</p>
        ) : orders.length === 0 ? (
          <p className="text-slate-500 text-sm">Nie masz jeszcze żadnych zamówień.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="text-xs uppercase bg-slate-50 text-slate-500">
                <tr>
                  <th className="px-4 py-3 rounded-l-lg">ID Zamówienia</th>
                  <th className="px-4 py-3">Ilość</th>
                  <th className="px-4 py-3">Kwota całkowita</th>
                  <th className="px-4 py-3 rounded-r-lg">Status</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id} className="border-b border-slate-100 last:border-0 hover:bg-slate-50">
                    <td className="px-4 py-3 font-medium text-slate-900">
                      #{order.id.slice(0, 8)}...
                    </td>
                    <td className="px-4 py-3">
                      {order.quantity ?? 1} szt.
                    </td>
                    <td className="px-4 py-3 font-semibold text-slate-900">
                      {order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'} PLN
                    </td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        order.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-800' : 
                        order.status === 'PENDING' ? 'bg-amber-100 text-amber-800' : 
                        'bg-slate-100 text-slate-800'
                      }`}>
                        {order.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};