import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { RegisterPage } from './RegisterPage';
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

const renderComponent = () =>
  render(
    <BrowserRouter>
      <RegisterPage />
    </BrowserRouter>
  );

describe('RegisterPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders form fields, labels, and submit button correctly', () => {
    renderComponent();

    expect(screen.getByRole('heading', { name: /stwórz konto/i })).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Jan')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Kowalski')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('jan@example.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /zarejestruj się/i })).toBeInTheDocument();
  });

  it('submits registration data successfully and redirects to login page', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });
    
    (api.post as any).mockResolvedValueOnce({ data: { success: true } });

    renderComponent();

    await user.type(screen.getByPlaceholderText('Jan'), 'Anna');
    await user.type(screen.getByPlaceholderText('Kowalski'), 'Nowak');
    await user.type(screen.getByPlaceholderText('jan@example.com'), 'anna@example.com');
    await user.type(screen.getByPlaceholderText('••••••••'), 'SecurePass123!');

    await user.click(screen.getByRole('button', { name: /zarejestruj się/i }));

    expect(api.post).toHaveBeenCalledWith('/users/register', {
      firstName: 'Anna',
      lastName: 'Nowak',
      email: 'anna@example.com',
      password: 'SecurePass123!',
    });

    expect(
      await screen.findByText(/konto zostało utworzone! przekierowywanie do logowania\.\.\./i)
    ).toBeInTheDocument();

    vi.advanceTimersByTime(2000);

    expect(mockNavigate).toHaveBeenCalledWith('/login');

    vi.useRealTimers();
  });

  it('displays API error message when registration fails', async () => {
    const user = userEvent.setup();
    const errorMessage = 'Użytkownik o podanym adresie email już istnieje';

    (api.post as any).mockRejectedValueOnce({
      response: {
        data: {
          message: errorMessage,
        },
      },
    });

    renderComponent();

    await user.type(screen.getByPlaceholderText('Jan'), 'Jan');
    await user.type(screen.getByPlaceholderText('Kowalski'), 'Kowalski');
    await user.type(screen.getByPlaceholderText('jan@example.com'), 'existing@example.com');
    await user.type(screen.getByPlaceholderText('••••••••'), 'Password123');

    await user.click(screen.getByRole('button', { name: /zarejestruj się/i }));

    expect(await screen.findByText(errorMessage)).toBeInTheDocument();
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  it('displays fallback error message when server responds without a specific message', async () => {
    const user = userEvent.setup();

    (api.post as any).mockRejectedValueOnce(new Error('Network error'));

    renderComponent();

    await user.type(screen.getByPlaceholderText('Jan'), 'Jan');
    await user.type(screen.getByPlaceholderText('Kowalski'), 'Kowalski');
    await user.type(screen.getByPlaceholderText('jan@example.com'), 'jan@example.com');
    await user.type(screen.getByPlaceholderText('••••••••'), 'Password123');

    await user.click(screen.getByRole('button', { name: /zarejestruj się/i }));

    expect(await screen.findByText('Błąd podczas rejestracji konta')).toBeInTheDocument();
  });
});