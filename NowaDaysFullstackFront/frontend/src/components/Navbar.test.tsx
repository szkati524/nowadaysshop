import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { Navbar } from './Navbar';
import * as AuthContextModule from '../context/AuthContext';
import * as CartContextModule from '../context/CartContext';



const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('Navbar Component', () => {
  const mockLogout = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });


  const renderNavbar = (
    userOverride: any = null,
    cartItemsOverride: any[] = []
  ) => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: userOverride,
      logout: mockLogout,
      login: vi.fn(),
      refreshBalance: vi.fn(),
      withdraw: vi.fn(),
    });

    vi.spyOn(CartContextModule, 'useCart').mockReturnValue({
      cart: cartItemsOverride,
      addToCart: vi.fn(),
      removeFromCart: vi.fn(),
      clearCart: vi.fn(),
      totalAmount: 0,
    });

    return render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    );
  };

  it('should render brand name and public links', () => {
    renderNavbar();

    expect(screen.getByText('NowadaysShop')).toBeInTheDocument();
    expect(screen.getByText('Sklep')).toBeInTheDocument();
    expect(screen.getByText('Kontakt')).toBeInTheDocument();
  });

  it('should render login and register links when user is not authenticated', () => {
    renderNavbar(null);

    expect(screen.getByText('Zaloguj się')).toBeInTheDocument();
    expect(screen.getByText('Zarejestruj się')).toBeInTheDocument();
    expect(screen.queryByText('Wyloguj')).not.toBeInTheDocument();
  });

  it('should render user profile info, balance, and cart badge when authenticated as standard user', () => {
    const mockUser = {
      id: '1',
      email: 'user@example.com',
      firstName: 'John',
      lastName: 'Doe',
      role: 'USER',
      balance: 150.5,
    };

    const mockCart = [
      { product: { id: '1', name: 'Item 1', price: 10 }, quantity: 2 },
      { product: { id: '2', name: 'Item 2', price: 20 }, quantity: 1 },
    ];

    renderNavbar(mockUser, mockCart);

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('150.50 PLN')).toBeInTheDocument();
    expect(screen.getByText('3')).toBeInTheDocument(); 
    expect(screen.queryByText('Dodaj produkt')).not.toBeInTheDocument();
  });

  it('should render "Dodaj produkt" button when authenticated user is an ADMIN', () => {
    const mockAdminUser = {
      id: '2',
      email: 'admin@example.com',
      firstName: 'Admin',
      lastName: 'User',
      role: 'ADMIN',
      balance: 0,
    };

    renderNavbar(mockAdminUser);

    expect(screen.getByText('Dodaj produkt')).toBeInTheDocument();
  });

  it('should call logout and navigate to home page when logout button is clicked', async () => {
    const mockUser = {
      id: '1',
      email: 'user@example.com',
      firstName: 'John',
      lastName: 'Doe',
      role: 'USER',
      balance: 100,
    };

    const user = userEvent.setup();
    renderNavbar(mockUser);

    const logoutButton = screen.getByRole('button', { name: /wyloguj/i });
    await user.click(logoutButton);

    expect(mockLogout).toHaveBeenCalledTimes(1);
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });
});