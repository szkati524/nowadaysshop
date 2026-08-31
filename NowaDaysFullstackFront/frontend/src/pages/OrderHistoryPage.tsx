import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { FiShoppingBag, FiCalendar, FiPackage, FiAlertCircle } from 'react-icons/fi';

interface OrderItem {
  id: string;
  userId: string;
  productId: string;
  productName?: string;
  quantity: number;
  totalPrice: number;
  status?: string;
  createdAt: string;
}

export const OrderHistoryPage: React.FC = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user?.id) return;

    setLoading(true);
    setError(null);

    api.get<OrderItem[]>(`/orders/user/${user.id}`)
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : [];
        const sortedOrders = data.sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        setOrders(sortedOrders);
      })
      .catch((err) => {
        console.error('Błąd podczas pobierania historii zamówień:', err);
        if (err.response?.status === 403) {
          setError('Brak uprawnień. Zaloguj się ponownie.');
        } else {
          setError('Nie udało się pobrać historii zamówień.');
        }
      })
      .finally(() => setLoading(false));
  }, [user]);

  if (!user) {
    return (
      <div className="container mx-auto px-4 py-12 text-center text-slate-600">
        Musisz się zalogować, aby zobaczyć historię zamówień.
      </div>
    );
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-12 text-center text-slate-500">
        Ładowanie historii zamówień...
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-10 max-w-4xl">
      <div className="flex items-center gap-3 mb-8">
        <div className="p-3 bg-indigo-50 text-indigo-600 rounded-full">
          <FiShoppingBag className="w-6 h-6" />
        </div>
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Historia Zamówień</h1>
          <p className="text-sm text-slate-500">Przeglądaj swoje złożone zamówienia</p>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center gap-2 text-sm">
          <FiAlertCircle className="shrink-0 w-5 h-5" />
          <span>{error}</span>
        </div>
      )}

      {orders.length === 0 ? (
        <div className="bg-white p-8 rounded-xl border border-slate-200 shadow-sm text-center">
          <FiPackage className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-lg font-medium text-slate-700">Brak historii zamówień</h3>
          <p className="text-slate-500 text-sm mt-1">Nie złożyłeś jeszcze żadnego zamówienia w naszym sklepie.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div
              key={order.id}
              className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm hover:shadow-md transition flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-slate-800 text-lg">
                    {order.productName || `Produkt ID: ${order.productId}`}
                  </span>
                  <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-green-50 text-green-700 border border-green-200">
                    {order.status || 'Zrealizowane'}
                  </span>
                </div>

                <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500">
                  <span className="flex items-center gap-1">
                    <FiCalendar />
                    {order.createdAt ? new Date(order.createdAt).toLocaleDateString('pl-PL', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                    }) : 'Brak daty'}
                  </span>
                  <span className="flex items-center gap-1 font-mono">
                    ID: {order.id?.substring(0, 8)}...
                  </span>
                </div>
              </div>

              <div className="flex items-center justify-between md:justify-end gap-6 border-t md:border-t-0 pt-3 md:pt-0 border-slate-100">
                <div className="text-left md:text-right">
                  <span className="block text-xs text-slate-400">Ilość: {order.quantity} szt.</span>
                  <span className="text-xl font-bold text-indigo-600">
                    {(order.totalPrice ?? 0).toFixed(2)} PLN
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};