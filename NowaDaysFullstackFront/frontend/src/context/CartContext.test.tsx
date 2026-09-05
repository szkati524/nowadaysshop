import '@testing-library/jest-dom';
import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { CartProvider, useCart } from './CartContext';
import * as AuthContextModule from './AuthContext';
import type { Product } from '../types';

vi.mock('./AuthContext', () => ({
  useAuth: vi.fn(),
}));

const mockProductA: Product = {
  id: 'p1',
  name: 'Myszka bezprzewodowa',
  price: 100,
  stockQuantity: 10,
};

const mockProductB: Product = {
  id: 'p2',
  name: 'Klawiatura mechaniczna',
  price: 250,
  stockQuantity: 5,
};

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <CartProvider>{children}</CartProvider>
);

describe('CartContext', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();

    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: { id: 'user-123', email: 'jan@example.com', firstName: 'Jan', lastName: 'Kowalski' },
      login: vi.fn(),
      logout: vi.fn(),
      refreshBalance: vi.fn(),
      withdraw: vi.fn(),
    });
  });

  it('powinien wyrzucić błąd przy użyciu useCart poza CartProvider', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    expect(() => renderHook(() => useCart())).toThrow(
      'useCart must be used within CartProvider'
    );

    consoleSpy.mockRestore();
  });

  it('powinien załadować pusty koszyk na starcie', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    expect(result.current.cart).toEqual([]);
    expect(result.current.totalAmount).toBe(0);
  });

  it('powinien dodawać nowy produkt do koszyka i obliczać sumę całkowitą', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => {
      result.current.addToCart(mockProductA, 2);
    });

    expect(result.current.cart).toHaveLength(1);
    expect(result.current.cart[0]).toEqual({
      product: mockProductA,
      quantity: 2,
    });
    expect(result.current.totalAmount).toBe(200);
  });

  it('powinien zwiększać ilość, jeśli ten sam produkt zostanie dodany ponownie', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => {
      result.current.addToCart(mockProductA, 1);
    });

    act(() => {
      result.current.addToCart(mockProductA, 3);
    });

    expect(result.current.cart).toHaveLength(1);
    expect(result.current.cart[0].quantity).toBe(4);
    expect(result.current.totalAmount).toBe(400);
  });

  it('powinien usuwać produkt z koszyka (removeFromCart)', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => {
      result.current.addToCart(mockProductA, 1);
      result.current.addToCart(mockProductB, 1);
    });

    expect(result.current.cart).toHaveLength(2);
    expect(result.current.totalAmount).toBe(350);

    act(() => {
      result.current.removeFromCart('p1');
    });

    expect(result.current.cart).toHaveLength(1);
    expect(result.current.cart[0].product.id).toBe('p2');
    expect(result.current.totalAmount).toBe(250);
  });

  it('powinien czyścić cały koszyk (clearCart)', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => {
      result.current.addToCart(mockProductA, 2);
      result.current.addToCart(mockProductB, 1);
    });

    expect(result.current.cart).toHaveLength(2);

    act(() => {
      result.current.clearCart();
    });

    expect(result.current.cart).toEqual([]);
    expect(result.current.totalAmount).toBe(0);
  });

  it('powinien izolować koszyki użytkowników w localStorage według Klucza (User ID / Guest)', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => {
      result.current.addToCart(mockProductA, 1);
    });

    const savedUserCart = localStorage.getItem('shopping_cart_user-123');
    expect(savedUserCart).not.toBeNull();
    expect(JSON.parse(savedUserCart!)[0].product.id).toBe('p1');
  });
});