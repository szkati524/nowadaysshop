import { test, expect } from '@playwright/test';

test.describe('Autentykacja i Rejestracja (E2E)', () => {
  const randomEmail = `user_${Date.now()}@test.com`;

  test('powinien zarejestrować nowego użytkownika i przekierować na stronę logowania', async ({ page }) => {
    await page.goto('/register');


    await page.getByPlaceholder('Jan').fill('Piotr');
    await page.getByPlaceholder('Kowalski').fill('Nowak');
    await page.getByPlaceholder('jan@example.com').fill(randomEmail);
    await page.getByPlaceholder('••••••••').fill('TajneHaslo123');


    await page.getByRole('button', { name: /zarejestruj się/i }).click();


    await expect(
      page.getByText(/konto zostało utworzone/i)
    ).toBeVisible();


    await expect(page).toHaveURL(/\/login/, { timeout: 5000 });
  });

  test('powinien zalogować użytkownika i pokazać elementy nawigacyjne', async ({ page }) => {
    await page.goto('/login');

    await page.getByPlaceholder('jan@example.com').fill(randomEmail);
    await page.getByPlaceholder('••••••••').fill('TajneHaslo123');

   
    await page.getByRole('button', { name: /zaloguj się/i }).click();


    await expect(page).toHaveURL('/');
  });
});