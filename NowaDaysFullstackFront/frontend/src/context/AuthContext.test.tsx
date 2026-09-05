import '@testing-library/jest-dom';
import { renderHook, act, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AuthProvider, useAuth } from './AuthContext';
import api from '../api/axios';

vi.mock('../api/axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <AuthProvider>{children}</AuthProvider>
);

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('powinien zgłosić błąd przy wywołaniu useAuth poza AuthProvider', () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    expect(() => renderHook(() => useAuth())).toThrow(
      'useAuth must be used within AuthProvider'
    );

    consoleSpy.mockRestore();
  });

  it('powinien załadować początkowego użytkownika z localStorage, jeśli istnieje', async () => {
    const mockUser = {
      id: '123',
      email: 'jan@example.com',
      firstName: 'Jan',
      lastName: 'Kowalski',
      balance: 200,
    };
    localStorage.setItem('user', JSON.stringify(mockUser));
    (api.get as any).mockResolvedValueOnce({ data: 250 });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.user?.balance).toBe(250);
    });
  });

  it('powinien poprawnie przeprowadzić logowanie (login) i zapisać token oraz dane w localStorage', async () => {
    const mockToken = 'header.eyJzdWIiOiIxMjMiLCJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20ifQ.signature';

    (api.post as any).mockResolvedValueOnce({
      data: {
        token: mockToken,
        user: {
          id: '123',
          email: 'jan@example.com',
          firstName: 'Jan',
          lastName: 'Kowalski',
          role: 'USER',
        },
      },
    });

    (api.get as any).mockResolvedValueOnce({ data: 150.0 });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.login('jan@example.com', 'haslo123');
    });

    expect(api.post).toHaveBeenCalledWith('/users/login', {
      email: 'jan@example.com',
      password: 'haslo123',
    });

    expect(localStorage.getItem('token')).toBe(mockToken);
    expect(result.current.user).toEqual({
      id: '123',
      email: 'jan@example.com',
      firstName: 'Jan',
      lastName: 'Kowalski',
      role: 'USER',
      balance: 150.0,
    });
  });

  it('powinien obsłużyć wylogowanie (logout) i usunąć dane z localStorage', async () => {
    const mockUser = {
      id: '123',
      email: 'jan@example.com',
      firstName: 'Jan',
      lastName: 'Kowalski',
      balance: 100,
    };
    localStorage.setItem('user', JSON.stringify(mockUser));
    localStorage.setItem('token', 'fake-jwt-token');

    (api.get as any).mockResolvedValueOnce({ data: 100 });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.user).not.toBeNull();
    });

    await act(async () => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('powinien odświeżyć saldo użytkownika (refreshBalance)', async () => {
    const mockUser = {
      id: '123',
      email: 'jan@example.com',
      firstName: 'Jan',
      lastName: 'Kowalski',
      balance: 100,
    };
    localStorage.setItem('user', JSON.stringify(mockUser));

    (api.get as any)
      .mockResolvedValueOnce({ data: 100 })
      .mockResolvedValueOnce({ data: 350 });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.user).not.toBeNull();
    });

    await act(async () => {
      await result.current.refreshBalance();
    });

    expect(result.current.user?.balance).toBe(350);
    expect(JSON.parse(localStorage.getItem('user')!).balance).toBe(350);
  });

  it('powinien zmniejszyć saldo lokalnie przy wypłacie (withdraw)', async () => {
    const mockUser = {
      id: '123',
      email: 'jan@example.com',
      firstName: 'Jan',
      lastName: 'Kowalski',
      balance: 100,
    };
    localStorage.setItem('user', JSON.stringify(mockUser));
    (api.get as any).mockResolvedValueOnce({ data: 100 });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.user).not.toBeNull();
    });

    await act(async () => {
      result.current.withdraw(40);
    });

    expect(result.current.user?.balance).toBe(60);
    expect(JSON.parse(localStorage.getItem('user')!).balance).toBe(60);
  });
});