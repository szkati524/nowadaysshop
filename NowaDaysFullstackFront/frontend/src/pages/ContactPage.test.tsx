import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { ContactPage } from './ContactPage';

describe('ContactPage Component', () => {
  it('should render main header and introductory text', () => {
    render(<ContactPage />);

    expect(
      screen.getByRole('heading', { level: 1, name: /skontaktuj się z nami/i })
    ).toBeInTheDocument();

    expect(
      screen.getByText(/jesteśmy tutaj, aby ci pomóc/i)
    ).toBeInTheDocument();
  });

  it('should render all contact method cards with correct information', () => {
    render(<ContactPage />);

    const emailLink = screen.getByRole('link', { name: /kontakt@nowadays.pl/i });
    expect(emailLink).toBeInTheDocument();
    expect(emailLink).toHaveAttribute('href', 'mailto:kontakt@nowadays.pl');

    const phoneLink = screen.getByRole('link', { name: /\+48 123 456 789/i });
    expect(phoneLink).toBeInTheDocument();
    expect(phoneLink).toHaveAttribute('href', 'tel:+48123456789');

    expect(screen.getByText(/nowadaysshop sp. z o.o./i)).toBeInTheDocument();
    expect(screen.getByText(/ul. zmyslona 12\/4/i)).toBeInTheDocument();

    expect(screen.getByText(/poniedziałek – piątek: 8:00 – 16:00/i)).toBeInTheDocument();
  });

  it('should render the FAQ section with questions and answers', () => {
    render(<ContactPage />);

    expect(
      screen.getByRole('heading', { level: 2, name: /często zadawane pytania/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { level: 4, name: /jak szybko realizujecie zamówienia\?/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { level: 4, name: /jakie są formy płatności\?/i })
    ).toBeInTheDocument();

    expect(
      screen.getByText(/wszystkie zamówienia złożone do godziny 12:00/i)
    ).toBeInTheDocument();
  });
});