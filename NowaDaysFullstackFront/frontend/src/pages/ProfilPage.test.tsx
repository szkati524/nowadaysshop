import '@testing-library/jest-dom';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ProfilePage } from './ProfilePage';
import * as AuthContextModule from '../context/AuthContext';
import * as CartContextModule from '../context/CartContext';
import api from '../api/axios';

vi.mock('../api/axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe('ProfilePage Component', () => {
  const mockRefreshBalance = vi.fn();
  const mockRemoveFromCart = vi.fn();
  const mockClearCart = vi.fn();

  const mockUser = {
    id: 'user-1',
    firstName: 'Jan',
    lastName: 'Kowalski',
    email: 'jan@example.com',
    balance: 100,
  };

  const mockCart = [
    {
      product: {
        id: 'p1',
        name: 'Myszka Gamingowa',
        price: 50,
      },
      quantity: 1,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: mockUser,
      refreshBalance: mockRefreshBalance,
      login: vi.fn(),
      logout: vi.fn(),
    } as any);

    vi.spyOn(CartContextModule, 'useCart').mockReturnValue({
      cart: mockCart,
      removeFromCart: mockRemoveFromCart,
      clearCart: mockClearCart,
      totalAmount: 50,
      addToCart: vi.fn(),
      totalPrice: 50,
    } as any);
  });

  it('should render login prompt when user is not logged in', () => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: null,
      refreshBalance: mockRefreshBalance,
      login: vi.fn(),
      logout: vi.fn(),
    } as any);

    render(<ProfilePage />);

    expect(screen.getByText('Musisz się zalogować.')).toBeInTheDocument();
  });

  it('should fetch and display user profile details and order history', async () => {
    const mockOrders = [
      {
        id: 'ord-12345678',
        quantity: 2,
        totalPrice: 100,
        status: 'COMPLETED',
      },
    ];

    (api.get as any).mockResolvedValueOnce({ data: mockOrders });

    render(<ProfilePage />);

  
    expect(screen.getByText('Jan Kowalski')).toBeInTheDocument();
    expect(screen.getByText('jan@example.com')).toBeInTheDocument();

   
    const walletHeader = screen.getByText('Dostępne środki');
    const walletContainer = walletHeader.closest('div')!;
    expect(within(walletContainer).getByText(/100\.00\s*PLN/i)).toBeInTheDocument();

  
    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/orders/user/user-1');
    });

  
    const orderRow = screen.getByRole('row', { name: /ord-1234/i });

    expect(within(orderRow).getByText(/#\s*ord-1234\.\.\./i)).toBeInTheDocument();
    expect(within(orderRow).getByText('2 szt.')).toBeInTheDocument();
    expect(within(orderRow).getByText(/100\.00\s*PLN/i)).toBeInTheDocument();
    expect(within(orderRow).getByText('COMPLETED')).toBeInTheDocument();
  });

  it('should handle wallet deposit successfully', async () => {
    const user = userEvent.setup();
    (api.get as any).mockResolvedValueOnce({ data: [] });
    (api.post as any).mockResolvedValueOnce({});

    render(<ProfilePage />);

    const depositHeader = screen.getByRole('heading', {
      name: /doładuj portfel/i,
    });
    const depositForm = depositHeader.closest('form')!;

    const depositInput = within(depositForm).getByRole('spinbutton');

    await user.clear(depositInput);
    await user.type(depositInput, '50');

    const depositButton = within(depositForm).getByRole('button', {
      name: /^doładuj$/i,
    });
    await user.click(depositButton);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/users/user-1/deposit', {
        amount: 50,
      });
      expect(mockRefreshBalance).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith(
        'Pomyślnie doładowano konto o 50.00 PLN!'
      );
    });
  });

  it('should handle funds withdrawal successfully', async () => {
    const user = userEvent.setup();
    (api.get as any).mockResolvedValueOnce({ data: [] });
    (api.post as any).mockResolvedValueOnce({});

    render(<ProfilePage />);

    const ibanInput = screen.getByPlaceholderText('Numer IBAN');
    const amountInput = screen.getByPlaceholderText('Kwota');

    await user.type(ibanInput, 'PL12345678901234567890123456');
    await user.type(amountInput, '30');

    const withdrawButton = screen.getByRole('button', { name: /^wypłać$/i });
    await user.click(withdrawButton);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/users/user-1/withdraw', {
        amount: 30,
      });
      expect(mockRefreshBalance).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith(
        'Pomyślnie zlecono wypłatę na konto PL12345678901234567890123456!'
      );
    });
  });

  it('should prevent withdrawal when amount exceeds user balance', async () => {
    const user = userEvent.setup();
    (api.get as any).mockResolvedValueOnce({ data: [] });

    render(<ProfilePage />);

    const ibanInput = screen.getByPlaceholderText('Numer IBAN');
    const amountInput = screen.getByPlaceholderText('Kwota');

    await user.type(ibanInput, 'PL12345678901234567890123456');
    await user.type(amountInput, '500');

    const withdrawButton = screen.getByRole('button', { name: /^wypłać$/i });
    await user.click(withdrawButton);

    expect(api.post).not.toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Nieprawidłowa kwota wypłaty.');
  });

  it('should process cart checkout successfully', async () => {
    const user = userEvent.setup();
    (api.get as any).mockResolvedValue({ data: [] });
    (api.post as any).mockResolvedValue({});

    render(<ProfilePage />);

    const checkoutButton = screen.getByRole('button', {
      name: /zapłać za koszyk/i,
    });
    await user.click(checkoutButton);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/orders', {
        productId: 'p1',
        quantity: 1,
      });
      expect(mockRefreshBalance).toHaveBeenCalled();
      expect(mockClearCart).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith(
        'Pomyślnie opłacono i złożono wszystkie zamówienia!'
      );
    });
  });

  it('should allow removing item from cart', async () => {
    const user = userEvent.setup();
    (api.get as any).mockResolvedValueOnce({ data: [] });

    render(<ProfilePage />);

    const removeButtons = screen.getAllByRole('button');

    const trashButton = removeButtons.find((btn) =>
      btn.className.includes('rose-500')
    );

    if (trashButton) {
      await user.click(trashButton);
      expect(mockRemoveFromCart).toHaveBeenCalledWith('p1');
    }
  });
});