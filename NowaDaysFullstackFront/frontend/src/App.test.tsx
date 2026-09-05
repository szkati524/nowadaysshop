import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from './App';
import api from './api/axios';

vi.mock('./api/axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe('App Routing and Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    (api.get as any).mockImplementation((url: string) => {
      if (url === '/products/categories') {
        return Promise.resolve({ data: ['Elektronika'] });
      }
      if (url === '/products/search') {
        return Promise.resolve({
          data: { content: [], totalPages: 1, totalElements: 0, size: 6, number: 0 },
        });
      }
      return Promise.resolve({ data: {} });
    });
  });

  it('renders shop page as default route and displays navbar', async () => {
    render(<App />);

    expect(screen.getByRole('heading', { name: /katalog produktów/i })).toBeInTheDocument();

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/products/categories');
    });
  });

  it('navigates to login page when clicking login link', async () => {
    const user = userEvent.setup();
    render(<App />);

    const loginLinks = screen.getAllByRole('link', { name: /zaloguj/i });
    await user.click(loginLinks[0]);

    expect(await screen.findByRole('heading', { name: /zaloguj się/i })).toBeInTheDocument();
  });

  it('navigates to registration page via register route', async () => {
    window.history.pushState({}, 'Register Test', '/register');
    window.dispatchEvent(new Event('popstate'));

    render(<App />);

    expect(await screen.findByRole('heading', { name: /stwórz konto/i })).toBeInTheDocument();
  });

  it('navigates to contact page', async () => {
    window.history.pushState({}, 'Contact Test', '/contact');
    window.dispatchEvent(new Event('popstate'));

    render(<App />);

   
    expect(await screen.findByRole('heading', { name: /kontakt/i })).toBeInTheDocument();
  });
});