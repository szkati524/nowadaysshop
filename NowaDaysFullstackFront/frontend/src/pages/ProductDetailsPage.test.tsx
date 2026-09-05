import '@testing-library/jest-dom';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ProductDetailsPage } from './ProductDetailsPage';
import * as CartContextModule from '../context/CartContext';
import api from '../api/axios';

const mockNavigate = vi.fn();
const mockParams = { id: 'prod-123' };

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => mockParams,
  };
});

vi.mock('../api/axios', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('ProductDetailsPage Component', () => {
  const mockAddToCart = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(window, 'alert').mockImplementation(() => {});
  });

  const renderProductDetailsPage = () => {
    vi.spyOn(CartContextModule, 'useCart').mockReturnValue({
      cart: [],
      addToCart: mockAddToCart,
      removeFromCart: vi.fn(),
      clearCart: vi.fn(),
      totalAmount: 0,
    });

    return render(<ProductDetailsPage />);
  };

  it('should display loading state initially', () => {
    (api.get as any).mockReturnValue(new Promise(() => {}));

    renderProductDetailsPage();

    expect(screen.getByText('Ładowanie szczegółów...')).toBeInTheDocument();
  });

  it('should display fallback message when product is not found', async () => {
    (api.get as any).mockResolvedValueOnce({ data: null });

    renderProductDetailsPage();

    await waitFor(() => {
      expect(screen.getByText('Nie znaleziono produktu.')).toBeInTheDocument();
    });
  });

  it('should fetch and display product details correctly', async () => {
    const mockProduct = {
      id: 'prod-123',
      name: 'Myszka Gamingowa',
      category: 'Elektronika',
      description: 'Precyzyjna mysz z podświetleniem RGB',
      price: 199.99,
      stockQuantity: 15,
    };

    (api.get as any).mockResolvedValueOnce({ data: mockProduct });

    renderProductDetailsPage();

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/products/prod-123');
    });

    expect(
      screen.getByRole('heading', { level: 1, name: 'Myszka Gamingowa' })
    ).toBeInTheDocument();
    expect(screen.getByText('Elektronika')).toBeInTheDocument();
    expect(
      screen.getByText('Precyzyjna mysz z podświetleniem RGB')
    ).toBeInTheDocument();
    expect(screen.getByText('199.99 PLN')).toBeInTheDocument();
    expect(screen.getByText('15 szt.')).toBeInTheDocument();
  });

  it('should update quantity state when input value changes', async () => {
    const mockProduct = {
      id: 'prod-123',
      name: 'Myszka Gamingowa',
      price: 100,
      stockQuantity: 10,
    };

    (api.get as any).mockResolvedValueOnce({ data: mockProduct });

    renderProductDetailsPage();

    const quantityInput = await screen.findByRole('spinbutton');
    fireEvent.change(quantityInput, { target: { value: '3' } });

    expect(quantityInput).toHaveValue(3);
  });

  it('should enforce minimum quantity of 1 when typing invalid numbers', async () => {
    const mockProduct = {
      id: 'prod-123',
      name: 'Myszka Gamingowa',
      price: 100,
      stockQuantity: 10,
    };

    (api.get as any).mockResolvedValueOnce({ data: mockProduct });

    renderProductDetailsPage();

    const quantityInput = await screen.findByRole('spinbutton');
    fireEvent.change(quantityInput, { target: { value: '-2' } });

    expect(quantityInput).toHaveValue(1);
  });

  it('should call addToCart and trigger alert when clicking "Dodaj do koszyka"', async () => {
    const user = userEvent.setup();
    const mockProduct = {
      id: 'prod-123',
      name: 'Myszka Gamingowa',
      price: 100,
      stockQuantity: 10,
    };

    (api.get as any).mockResolvedValueOnce({ data: mockProduct });

    renderProductDetailsPage();

    const quantityInput = await screen.findByRole('spinbutton');
    fireEvent.change(quantityInput, { target: { value: '2' } });

    const addToCartButton = screen.getByRole('button', {
      name: /dodaj do koszyka/i,
    });
    await user.click(addToCartButton);

    expect(mockAddToCart).toHaveBeenCalledWith(mockProduct, 2);
    expect(window.alert).toHaveBeenCalledWith('Dodano produkt do koszyka!');
  });

  it('should navigate back to shop when clicking "Powrót do sklepu"', async () => {
    const user = userEvent.setup();
    const mockProduct = {
      id: 'prod-123',
      name: 'Myszka Gamingowa',
      price: 100,
      stockQuantity: 10,
    };

    (api.get as any).mockResolvedValueOnce({ data: mockProduct });

    renderProductDetailsPage();

    const backButton = await screen.findByRole('button', {
      name: /powrót do sklepu/i,
    });
    await user.click(backButton);

    expect(mockNavigate).toHaveBeenCalledWith('/shop');
  });
});