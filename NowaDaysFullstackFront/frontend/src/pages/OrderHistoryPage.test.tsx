import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { OrderHistoryPage } from './OrderHistoryPage';
import * as AuthContextModule from '../context/AuthContext';
import api from '../api/axios';

vi.mock('../api/axios', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('OrderHistoryPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderOrderHistoryPage = (userOverride: any = null) => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: userOverride,
      logout: vi.fn(),
      login: vi.fn(),
      refreshBalance: vi.fn(),
      withdraw: vi.fn(),
    });

    return render(<OrderHistoryPage />);
  };

  it('should render login message when user is not logged in', () => {
    renderOrderHistoryPage(null);

    expect(
      screen.getByText('Musisz się zalogować, aby zobaczyć historię zamówień.')
    ).toBeInTheDocument();
  });

  it('should display loading state initially when logged in', () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    (api.get as any).mockReturnValue(new Promise(() => {}));

    renderOrderHistoryPage(user);

    expect(
      screen.getByText('Ładowanie historii zamówień...')
    ).toBeInTheDocument();
  });

  it('should fetch, sort, and render user orders successfully', async () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    const mockOrders = [
      {
        id: 'order-11111111',
        userId: 'user-123',
        productId: 'prod-1',
        productName: 'Myszka Bezprzewodowa',
        quantity: 2,
        totalPrice: 259.98,
        status: 'Zrealizowane',
        createdAt: '2023-01-01T10:00:00.000Z',
      },
      {
        id: 'order-22222222',
        userId: 'user-123',
        productId: 'prod-2',
        productName: 'Klawiatura Mechaniczna',
        quantity: 1,
        totalPrice: 450.0,
        status: 'W trakcie',
        createdAt: '2023-06-15T12:00:00.000Z',
      },
    ];

    (api.get as any).mockResolvedValueOnce({ data: mockOrders });

    renderOrderHistoryPage(user);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/orders/user/user-123');
    });

    expect(
      screen.getByRole('heading', { name: /historia zamówień/i })
    ).toBeInTheDocument();

    expect(screen.getByText('Myszka Bezprzewodowa')).toBeInTheDocument();
    expect(screen.getByText('Klawiatura Mechaniczna')).toBeInTheDocument();

    expect(screen.getByText('259.98 PLN')).toBeInTheDocument();
    expect(screen.getByText('450.00 PLN')).toBeInTheDocument();

    expect(screen.getByText('Ilość: 2 szt.')).toBeInTheDocument();
    expect(screen.getByText('Ilość: 1 szt.')).toBeInTheDocument();

    const renderedHeadings = screen.getAllByText(/(Myszka|Klawiatura)/i);
    expect(renderedHeadings[0]).toHaveTextContent('Klawiatura Mechaniczna');
    expect(renderedHeadings[1]).toHaveTextContent('Myszka Bezprzewodowa');
  });

  it('should fallback to product ID when productName is missing', async () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    const mockOrders = [
      {
        id: 'order-33333333',
        userId: 'user-123',
        productId: 'prod-xyz-99',
        quantity: 1,
        totalPrice: 100.0,
        createdAt: '2023-05-01T10:00:00.000Z',
      },
    ];

    (api.get as any).mockResolvedValueOnce({ data: mockOrders });

    renderOrderHistoryPage(user);

    await waitFor(() => {
      expect(screen.getByText('Produkt ID: prod-xyz-99')).toBeInTheDocument();
    });
  });

  it('should display empty history state when response contains no orders', async () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    (api.get as any).mockResolvedValueOnce({ data: [] });

    renderOrderHistoryPage(user);

    await waitFor(() => {
      expect(screen.getByText('Brak historii zamówień')).toBeInTheDocument();
    });

    expect(
      screen.getByText('Nie złożyłeś jeszcze żadnego zamówienia w naszym sklepie.')
    ).toBeInTheDocument();
  });

  it('should display 403 authorization error message when server returns 403 status', async () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    (api.get as any).mockRejectedValueOnce({
      response: { status: 403 },
    });

    renderOrderHistoryPage(user);

    await waitFor(() => {
      expect(
        screen.getByText('Brak uprawnień. Zaloguj się ponownie.')
      ).toBeInTheDocument();
    });
  });

  it('should display generic error message when server fails with non-403 error', async () => {
    const user = { id: 'user-123', email: 'user@example.com' };
    (api.get as any).mockRejectedValueOnce(new Error('Internal Server Error'));

    renderOrderHistoryPage(user);

    await waitFor(() => {
      expect(
        screen.getByText('Nie udało się pobrać historii zamówień.')
      ).toBeInTheDocument();
    });
  });
});