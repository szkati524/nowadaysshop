import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AddProduct } from './AddProductPage';
import * as AuthContextModule from '../context/AuthContext';
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
    post: vi.fn(),
  },
}));

describe('AddProduct Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderAddProduct = (userOverride: any = null) => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: userOverride,
      logout: vi.fn(),
      login: vi.fn(),
      refreshBalance: vi.fn(),
      withdraw: vi.fn(),
    });

    return render(
      <BrowserRouter>
        <AddProduct />
      </BrowserRouter>
    );
  };

  it('should render access denied message when user is not logged in', () => {
    renderAddProduct(null);

    expect(screen.getByText('Brak Dostępu')).toBeInTheDocument();
    expect(
      screen.getByText('Ta sekcja wymaga uprawnień administratora.')
    ).toBeInTheDocument();
  });

  it('should render access denied message for a non-admin user', () => {
    const standardUser = { id: '1', role: 'USER', email: 'user@example.com' };
    renderAddProduct(standardUser);

    expect(screen.getByText('Brak Dostępu')).toBeInTheDocument();
  });

  it('should navigate to home page when clicking "Wróć do sklepu" on access denied view', async () => {
    const user = userEvent.setup();
    renderAddProduct(null);

    const backButton = screen.getByRole('button', { name: /wróć do sklepu/i });
    await user.click(backButton);

    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('should render the add product form when user is an admin', () => {
    const adminUser = { id: '2', role: 'ADMIN', email: 'admin@example.com' };
    const { container } = renderAddProduct(adminUser);

    expect(screen.getByRole('heading', { name: /dodaj nowy produkt/i })).toBeInTheDocument();
    expect(container.querySelector('[name="name"]')).toBeInTheDocument();
    expect(container.querySelector('[name="category"]')).toBeInTheDocument();
    expect(container.querySelector('[name="description"]')).toBeInTheDocument();
    expect(container.querySelector('[name="price"]')).toBeInTheDocument();
    expect(container.querySelector('[name="initialStock"]')).toBeInTheDocument();
  });

  it('should submit correct form data and navigate to home page on success', async () => {
    const user = userEvent.setup();
    const adminUser = { id: '2', role: 'ADMIN', email: 'admin@example.com' };
    (api.post as any).mockResolvedValueOnce({ data: { id: '100' } });

    const { container } = renderAddProduct(adminUser);

    const nameInput = container.querySelector('[name="name"]')!;
    const categoryInput = container.querySelector('[name="category"]')!;
    const descriptionInput = container.querySelector('[name="description"]')!;
    const priceInput = container.querySelector('[name="price"]')!;

    await user.type(nameInput, 'Myszka Bezprzewodowa');
    await user.type(categoryInput, 'Elektronika');
    await user.type(descriptionInput, 'Cicha mysz z DPI 1600');
    
    await user.clear(priceInput);
    await user.type(priceInput, '129.99');

    const submitButton = screen.getByRole('button', { name: /dodaj produkt/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/products', {
        name: 'Myszka Bezprzewodowa',
        category: 'Elektronika',
        description: 'Cicha mysz z DPI 1600',
        price: 129.99,
        initialStock: 1,
      });
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });

  it('should display an error message when the API request fails', async () => {
    const user = userEvent.setup();
    const adminUser = { id: '2', role: 'ADMIN', email: 'admin@example.com' };
    
    (api.post as any).mockRejectedValueOnce({
      response: {
        data: {
          message: 'Produkt o podanej nazwie już istnieje.',
        },
      },
    });

    const { container } = renderAddProduct(adminUser);

    const nameInput = container.querySelector('[name="name"]')!;
    const categoryInput = container.querySelector('[name="category"]')!;

    await user.type(nameInput, 'Klawiatura');
    await user.type(categoryInput, 'Elektronika');

    const submitButton = screen.getByRole('button', { name: /dodaj produkt/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(
        screen.getByText('Produkt o podanej nazwie już istnieje.')
      ).toBeInTheDocument();
    });
  });
});