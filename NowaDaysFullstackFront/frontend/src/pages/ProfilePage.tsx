import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { FiUser, FiCreditCard, FiDollarSign, FiPlusCircle, FiMinusCircle } from 'react-icons/fi';

export const ProfilePage: React.FC = () => {
  const { user, refreshBalance } = useAuth();
 
  const [depositAmount, setDepositAmount] = useState<string>('50');
  const [withdrawAmount, setWithdrawAmount] = useState<string>('');
  const [iban, setIban] = useState<string>('');
  
  const [loadingDeposit, setLoadingDeposit] = useState(false);
  const [loadingWithdraw, setLoadingWithdraw] = useState(false);

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

    if (!user.id) {
      alert('Błąd: brak identyfikatora użytkownika. Zaloguj się ponownie.');
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
    const currentBalance = user.balance ?? 0;

    if (isNaN(numericAmount) || numericAmount <= 0) {
      alert('Wprowadź prawidłową kwotę większą od 0.');
      return;
    }

    if (numericAmount > currentBalance) {
      alert('Brak wystarczających środków na koncie.');
      return;
    }

    if (!iban || iban.trim().length < 10) {
      alert('Wprowadź poprawny numer konta bankowego (IBAN).');
      return;
    }

    if (!user.id) {
      alert('Błąd: brak identyfikatora użytkownika. Zaloguj się ponownie.');
      return;
    }

    setLoadingWithdraw(true);

    try {
      // Wywołanie backendowego endpointu Spring Boot do wypłaty
      await api.post(`/users/${user.id}/withdraw`, { amount: numericAmount });
      
      // Pobranie aktualnego salda po zmianach w bazie danych
      await refreshBalance(); 

      alert(`Pomyślnie zlecono wypłatę ${numericAmount.toFixed(2)} PLN na konto ${iban}!`);
      setWithdrawAmount('');
      setIban('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Błąd podczas wykonywania wypłaty.');
    } finally {
      setLoadingWithdraw(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-10 max-w-2xl">
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 mb-6">
        
        <div className="flex items-center gap-4 border-b border-slate-100 pb-6 mb-6">
          <div className="w-16 h-16 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center text-2xl font-bold">
            {user.firstName ? user.firstName[0] : <FiUser />}
          </div>
          <div>
            <h1 className="text-2xl font-bold text-slate-900">{user.firstName} {user.lastName}</h1>
            <p className="text-slate-500 text-sm">{user.email}</p>
            <span className="inline-block text-xs bg-slate-100 text-slate-600 px-2 py-0.5 rounded mt-1 font-mono">
              ID: {user.id}
            </span>
          </div>
        </div>

        <div className="bg-slate-50 p-6 rounded-xl border border-slate-200 flex items-center justify-between mb-8">
          <div>
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Dostępne środki</span>
            <div className="text-3xl font-extrabold text-indigo-600 mt-1">
              {user.balance !== undefined ? user.balance.toFixed(2) : '0.00'} PLN
            </div>
          </div>
          <FiCreditCard className="w-10 h-10 text-slate-300" />
        </div>

        <form onSubmit={handleDeposit} className="space-y-4 mb-10 pb-8 border-b border-slate-100">
          <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
            <FiPlusCircle className="text-emerald-600" /> Doładuj Portfel
          </h3>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Kwota doładowania (PLN)</label>
            <div className="relative">
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={depositAmount}
                onChange={(e) => setDepositAmount(e.target.value)}
                placeholder="0.00"
                className="w-full pl-10 pr-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                required
              />
              <FiDollarSign className="absolute left-3 top-3.5 text-slate-400" />
            </div>
          </div>

          <button
            type="submit"
            disabled={loadingDeposit || !depositAmount}
            className="w-full bg-emerald-600 text-white py-3 rounded-lg font-medium hover:bg-emerald-700 transition disabled:opacity-50"
          >
            {loadingDeposit ? 'Przetwarzanie...' : 'Zrealizuj doładowanie (Deposit)'}
          </button>
        </form>

        <form onSubmit={handleWithdraw} className="space-y-4">
          <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
            <FiMinusCircle className="text-rose-600" /> Wypłać na Konto Bankowe
          </h3>
          
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Numer konta bankowego (IBAN)</label>
            <input
              type="text"
              value={iban}
              onChange={(e) => setIban(e.target.value)}
              placeholder="PL 00 0000 0000 0000 0000 0000 0000"
              className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none font-mono text-sm"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Kwota wypłaty (PLN)</label>
            <div className="relative">
              <input
                type="number"
                min="0.01"
                max={user.balance ?? 0}
                step="0.01"
                value={withdrawAmount}
                onChange={(e) => setWithdrawAmount(e.target.value)}
                placeholder="0.00"
                className="w-full pl-10 pr-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                required
              />
              <FiDollarSign className="absolute left-3 top-3.5 text-slate-400" />
            </div>
          </div>

          <button
            type="submit"
            disabled={loadingWithdraw || !withdrawAmount || !iban}
            className="w-full bg-slate-800 text-white py-3 rounded-lg font-medium hover:bg-slate-900 transition disabled:opacity-50"
          >
            {loadingWithdraw ? 'Zlecanie przelewu...' : 'Wypłać środki'}
          </button>
        </form>

      </div>
    </div>
  );
};