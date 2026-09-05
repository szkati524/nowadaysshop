import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { LoginPage } from './LoginPage';
import * as AuthContextModule from '../context/AuthContext';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('LoginPage Component', () => {
  const mockLogin = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderLoginPage = () => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user: null,
      login: mockLogin,
      logout: vi.fn(),
      refreshBalance: vi.fn(),
      withdraw: vi.fn(),
    });

    return render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );
  };

  it('should render the login form elements correctly', () => {
    const { container } = renderLoginPage();

    expect(screen.getByRole('heading', { level: 1, name: /zaloguj się/i })).toBeInTheDocument();
    expect(screen.getByPlaceholderText('jan@example.com')).toBeInTheDocument();
    expect(container.querySelector('input[type="password"]')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /zaloguj się/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /zarejestruj się/i })).toHaveAttribute('href', '/register');
  });

  it('should update input fields on user typing', async () => {
    const user = userEvent.setup();
    const { container } = renderLoginPage();

    const emailInput = screen.getByPlaceholderText('jan@example.com');
    const passwordInput = container.querySelector('input[type="password"]')!;

    await user.type(emailInput, 'user@example.com');
    await user.type(passwordInput, 'secret123');

    expect(emailInput).toHaveValue('user@example.com');
    expect(passwordInput).toHaveValue('secret123');
  });

  it('should call login function and navigate to home page on successful submission', async () => {
    const user = userEvent.setup();
    mockLogin.mockResolvedValueOnce(undefined);
    const { container } = renderLoginPage();

    const emailInput = screen.getByPlaceholderText('jan@example.com');
    const passwordInput = container.querySelector('input[type="password"]')!;

    await user.type(emailInput, 'user@example.com');
    await user.type(passwordInput, 'secret123');
    await user.click(screen.getByRole('button', { name: /zaloguj się/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('user@example.com', 'secret123');
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });

  it('should display custom server error message when login fails with server error response', async () => {
    const user = userEvent.setup();
    mockLogin.mockRejectedValueOnce({
      response: {
        data: {
          message: 'Konto zostało zablokowane',
        },
      },
    });

    const { container } = renderLoginPage();

    const emailInput = screen.getByPlaceholderText('jan@example.com');
    const passwordInput = container.querySelector('input[type="password"]')!;

    await user.type(emailInput, 'blocked@example.com');
    await user.type(passwordInput, 'password');
    await user.click(screen.getByRole('button', { name: /zaloguj się/i }));

    await waitFor(() => {
      expect(screen.getByText('Konto zostało zablokowane')).toBeInTheDocument();
    });
  });

  it('should display default fallback error message when login fails without server response message', async () => {
    const user = userEvent.setup();
    mockLogin.mockRejectedValueOnce(new Error('Network Error'));

    const { container } = renderLoginPage();

    const emailInput = screen.getByPlaceholderText('jan@example.com');
    const passwordInput = container.querySelector('input[type="password"]')!;

    await user.type(emailInput, 'wrong@example.com');
    await user.type(passwordInput, 'wrongpass');
    await user.click(screen.getByRole('button', { name: /zaloguj się/i }));

    await waitFor(() => {
      expect(screen.getByText('Nieprawidłowy e-mail lub hasło')).toBeInTheDocument();
    });
  });

  it('should disable submit button and show loading state during request', async () => {
    const user = userEvent.setup();
    mockLogin.mockImplementationOnce(() => new Promise((resolve) => setTimeout(resolve, 100)));

    const { container } = renderLoginPage();

    const emailInput = screen.getByPlaceholderText('jan@example.com');
    const passwordInput = container.querySelector('input[type="password"]')!;

    await user.type(emailInput, 'user@example.com');
    await user.type(passwordInput, 'secret123');

    const submitButton = screen.getByRole('button', { name: /zaloguj się/i });
    await user.click(submitButton);

    expect(screen.getByRole('button', { name: /logowanie\.\.\./i })).toBeDisabled();
  });
});