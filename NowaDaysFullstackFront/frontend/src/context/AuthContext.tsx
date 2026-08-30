import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  balance?: number;
}

export interface AuthResponse {
  token?: string;
  id?: string | number;
  userId?: string | number;
  _id?: string | number;
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: any;
  user?: {
    id?: string | number;
    userId?: string | number;
    _id?: string | number;
    email?: string;
    firstName?: string;
    lastName?: string;
    role?: any;
  };
}

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshBalance: () => Promise<void>;
  withdraw: (amount: number) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const parseJwt = (token: string) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  const refreshBalance = async () => {
    if (!user?.id) return;
    try {
      const res = await api.get<number>(`/users/${user.id}/balance`);
      setUser((prevUser) => {
        if (!prevUser) return null;
        const updatedUser = { ...prevUser, balance: res.data };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        return updatedUser;
      });
    } catch (err) {
      console.error('Błąd odświeżania salda:', err);
    }
  };

  const withdraw = (amount: number) => {
    setUser((prevUser) => {
      if (!prevUser) return null;
      const currentBal = prevUser.balance ?? 0;
      const updatedBalance = Math.max(0, currentBal - amount);
      const updatedUser = { ...prevUser, balance: updatedBalance };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      return updatedUser;
    });
  };

  const login = async (email: string, password: string) => {
    const res = await api.post<AuthResponse>('/users/login', {
      email,
      password,
    });

    console.log('Odpowiedź z backendu:', res.data);

    const token = res.data.token;
    if (token) {
      localStorage.setItem('token', token);
    }

    const rawUserData = res.data.user || res.data;
    const tokenPayload = token ? parseJwt(token) : null;

    const rawId =
      rawUserData.id ??
      rawUserData.userId ??
      rawUserData._id ??
      tokenPayload?.id ??
      tokenPayload?.userId ??
      tokenPayload?._id ??
      tokenPayload?.sub;

    if (rawId === undefined || rawId === null) {
      console.error('Brak identyfikatora w strukturze:', res.data);
      throw new Error('Błąd autentykacji: backend nie dostarczył ID użytkownika.');
    }

    const userId = String(rawId);

    let currentBalance = 0;
    try {
      const balanceRes = await api.get<number>(`/users/${userId}/balance`, {
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      });
      currentBalance = balanceRes.data;
    } catch (e) {
      console.warn('Nie udało się pobrać początkowego salda:', e);
    }

    const userData: User = {
      id: userId,
      email: rawUserData.email || tokenPayload?.email || email,
      firstName: rawUserData.firstName || tokenPayload?.firstName || '',
      lastName: rawUserData.lastName || tokenPayload?.lastName || '',
      role: typeof rawUserData.role === 'string'
        ? rawUserData.role
        : JSON.stringify(rawUserData.role || tokenPayload?.role || ''),
      balance: currentBalance,
    };

    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  useEffect(() => {
    if (user?.id) {
      refreshBalance();
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, refreshBalance, withdraw }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};