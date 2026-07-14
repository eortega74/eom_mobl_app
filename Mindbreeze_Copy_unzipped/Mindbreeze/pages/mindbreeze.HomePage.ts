import { Page } from '@playwright/test';
import { MINDBREEZE_SELECTORS } from '../Selectors/mindbreeze.selectors';

export class MindbreezeHomePage {
  constructor(private page: Page) {}

  async navigateToHome() {
    await this.page.goto('/');
  }

  async search(searchText: string) {
    await this.page.locator(MINDBREEZE_SELECTORS.search.input).fill(searchText);
  }

  async openFirstPersonDetails(searchText: string) {
    const firstPersonName = this.page.locator(MINDBREEZE_SELECTORS.search.results)
      .filter({ hasText: new RegExp(searchText, 'i') })
      .first();
    const personName = (await firstPersonName.innerText()).trim();

    await firstPersonName.click();

    return personName;
  }
}
