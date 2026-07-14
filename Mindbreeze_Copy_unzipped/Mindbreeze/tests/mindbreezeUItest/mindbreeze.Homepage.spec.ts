import { test, expect } from '@playwright/test';
import { mindbreezeSearchData } from '../../Data/mindbreeze.data';
import { MindbreezeHomePage } from '../../pages/mindbreeze.HomePage';
import { MINDBREEZE_SELECTORS } from '../../Selectors/mindbreeze.selectors';

test.describe('Mindbreeze homepage', () => {
  test('can search from the homepage', async ({ page }) => {
    const mindbreezeHome = new MindbreezeHomePage(page);

    await mindbreezeHome.navigateToHome();
    await mindbreezeHome.search(mindbreezeSearchData.defaultSearch);

    await expect(page.locator(MINDBREEZE_SELECTORS.search.results).filter({ hasText: /testa/i }).first()).toBeVisible();

    const personName = await mindbreezeHome.openFirstPersonDetails(mindbreezeSearchData.defaultSearch);

    await expect(page.getByText(personName).first()).toBeVisible();
    await expect(page.locator('svg').first()).toBeVisible();
    await page.locator('svg').first().click();
  });
});
