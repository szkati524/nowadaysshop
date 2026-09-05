import { test, expect } from '@playwright/test';

test.describe('Katalog Produktów i Koszyk (E2E)', () => {
  test.beforeEach(async ({ page }) => {
    
    await page.goto('/login');
    await page.getByPlaceholder('jan@example.com').fill('jan@example.com');
    await page.getByPlaceholder('••••••••').fill('Password123');
    await page.getByRole('button', { name: /zaloguj się/i }).click();
  });

  test('powinien filtrować produkty po nazwie i dodać produkt do koszyka', async ({ page }) => {
    await page.goto('/');

    const searchInput = page.getByPlaceholder('Szukaj po nazwie...');
    await searchInput.fill('Myszka');
    await page.getByRole('button', { name: /filtruj/i }).click();

    
    const productCard = page.locator('div').filter({ hasText: 'Myszka' }).first();
    await expect(productCard).toBeVisible();

 
    await productCard.getByRole('button', { name: /do koszyka/i }).click();


    await page.goto('/profile');

  
    await expect(page.getByText('Myszka')).toBeVisible();
  });

  test('powinien doładować konto i sfinalizować płatność za koszyk', async ({ page }) => {
    await page.goto('/profile');


    page.on('dialog', async (dialog) => {
      await dialog.accept();
    });

   
    const depositInput = page.locator('input[type="number"]').first();
    await depositInput.fill('500');
    await page.getByRole('button', { name: /^doładuj$/i }).click();

    
    const payCartButton = page.getByRole('button', { name: /zapłać za koszyk/i });
    if (await payCartButton.isVisible()) {
      await payCartButton.click();
    }
  });
});