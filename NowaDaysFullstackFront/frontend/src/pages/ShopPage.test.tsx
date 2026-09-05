import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ShopPage } from './ShopPage';
import * as AuthContextModule from '../context/AuthContext';
import * as CartContextModule from '../context/CartContext';
import api from '../api/axios';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

vi.mock('../api/axios', () => ({
  default: {
    get: vi.fn(),
  },
}));

const mockAddToCart = vi.fn();

const renderComponent = () =>
  render(
    <BrowserRouter>
      <ShopPage />
    </BrowserRouter>
  );

describe('ShopPage Component', () => {
  const mockCategories = ['Elektronika', 'Książki'];
  
  const mockPagedResult = {
    content: [
      {
        id: 'p1',
        name: 'Myszka Gamingowa',
        category: 'Elektronika',
        price: 150.0,
        stockQuantity: 10,
      },
      {
        id: 'p2',
        name: 'Klawiatura Mechaniczna',
        category: 'Elektronika',
        price: 300.0,
        stockQuantity: 5,
      },
    ],
    totalPages: 2,
    totalElements: 12,
    size: 6,
    number: 0,
  };

  beforeEach(() => {
    vi.clearAllMocks();

    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: { id: 'u1', firstName: 'Jan', lastName: 'Kowalski', email: 'jan@example.com', balance: 100 },
      login: vi.fn(),
      logout: vi.fn(),
      refreshBalance: vi.fn(),
    } as any);

    vi.spyOn(CartContextModule, 'useCart').mockReturnValue({
      cart: [],
      addToCart: mockAddToCart,
      removeFromCart: vi.fn(),
      clearCart: vi.fn(),
      totalAmount: 0,
      totalPrice: 0,
    } as any);

    (api.get as any).mockImplementation((url: string) => {
      if (url === '/products/categories') {
        return Promise.resolve({ data: mockCategories });
      }
      if (url === '/products/search') {
        return Promise.resolve({ data: mockPagedResult });
      }
      return Promise.reject(new Error('Not found'));
    });
  });

  it('fetches and renders categories and products on mount', async () => {
    renderComponent();

    expect(screen.getByText('Ładowanie produktów...')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
      expect(screen.getByText('Klawiatura Mechaniczna')).toBeInTheDocument();
    });

    expect(api.get).toHaveBeenCalledWith('/products/categories');
    expect(api.get).toHaveBeenCalledWith('/products/search', {
      params: { page: 0, size: 6 },
    });

    expect(screen.getByRole('option', { name: 'Elektronika' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'Książki' })).toBeInTheDocument();
  });

  it('filters products by category selection', async () => {
    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
    });

    const categorySelect = screen.getByRole('combobox');
    await user.selectOptions(categorySelect, 'Elektronika');

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/products/search', {
        params: { page: 0, size: 6, category: 'Elektronika' },
      });
    });
  });

  it('filters products by name and price inputs on form submission', async () => {
    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
    });

    const nameInput = screen.getByPlaceholderText('Szukaj po nazwie...');
    const minPriceInput = screen.getByPlaceholderText('Cena od');
    const maxPriceInput = screen.getByPlaceholderText('Cena do');
    const filterButton = screen.getByRole('button', { name: /filtruj/i });

    await user.type(nameInput, 'Myszka');
    await user.type(minPriceInput, '50');
    await user.type(maxPriceInput, '200');

    await user.click(filterButton);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/products/search', {
        params: {
          page: 0,
          size: 6,
          name: 'Myszka',
          minPrice: 50,
          maxPrice: 200,
        },
      });
    });
  });

  it('navigates to product detail page when clicking on a product card', async () => {
    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
    });

    const productCard = screen.getByText('Myszka Gamingowa').closest('div');
    if (productCard) {
      await user.click(productCard);
    }

    expect(mockNavigate).toHaveBeenCalledWith('/products/p1');
  });

  it('adds item to cart when logged in', async () => {
    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
    });

    const addToCartButtons = screen.getAllByRole('button', { name: /do koszyka/i });
    await user.click(addToCartButtons[0]);

    expect(mockAddToCart).toHaveBeenCalledWith(mockPagedResult.content[0]);
    expect(mockNavigate).not.toHaveBeenCalledWith('/login');
  });

  it('redirects to login page when unauthenticated user clicks add to cart', async () => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: null,
      login: vi.fn(),
      logout: vi.fn(),
      refreshBalance: vi.fn(),
    } as any);

    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Myszka Gamingowa')).toBeInTheDocument();
    });

    const addToCartButtons = screen.getAllByRole('button', { name: /do koszyka/i });
    await user.click(addToCartButtons[0]);

    expect(mockAddToCart).not.toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('handles pagination navigation', async () => {
    const user = userEvent.setup();
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Strona 1 z 2')).toBeInTheDocument();
    });

    const buttons = screen.getAllByRole('button');
    const nextPageButton = buttons[buttons.length - 1]; 

    await user.click(nextPageButton);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/products/search', {
        params: { page: 1, size: 6 },
      });
    });
  });
});