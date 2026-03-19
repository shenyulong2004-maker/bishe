const { test, expect } = require('@playwright/test');

const menusFixture = JSON.stringify([
  { roleName: '学生', tableName: 'xuesheng', hasFrontLogin: '是' },
  { roleName: '教师', tableName: 'jiaoshi', hasFrontLogin: '是' },
]);

test.beforeEach(async ({ page }) => {
  await page.addInitScript((menus) => {
    localStorage.setItem('menus', menus);
  }, menusFixture);
});

test('桌面端布局可用且无横向溢出', async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 720 });
  await page.goto('/#/login');

  await expect(page.getByRole('heading', { name: '登录' })).toBeVisible();
  await expect(page.getByLabel('账号')).toBeVisible();
  await expect(page.getByLabel('密码')).toBeVisible();

  const hasOverflow = await page.evaluate(() => {
    return document.documentElement.scrollWidth > window.innerWidth + 1;
  });
  expect(hasOverflow).toBeFalsy();
});

test('移动端布局可用且无横向溢出', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto('/#/login');

  await expect(page.getByRole('heading', { name: '登录' })).toBeVisible();

  const hasOverflow = await page.evaluate(() => {
    return document.documentElement.scrollWidth > window.innerWidth + 1;
  });
  expect(hasOverflow).toBeFalsy();
});

test('键盘可达：Tab 顺序可聚焦到账号/密码/用户类型/记住密码/登录', async ({ page }) => {
  await page.goto('/#/login');

  await page.keyboard.press('Tab');
  await expect(page.getByLabel('账号')).toBeFocused();

  await page.keyboard.press('Tab');
  await expect(page.getByLabel('密码')).toBeFocused();

  await page.keyboard.press('Tab');
  await expect(page.getByLabel('用户类型')).toBeFocused();

  await page.keyboard.press('Tab');
  await expect(page.getByText('记住密码')).toBeVisible();

  await page.keyboard.press('Tab');
  await expect(page.getByRole('button', { name: '登录' })).toBeFocused();
});

test('表单可用性：空提交会出现可读错误提示', async ({ page }) => {
  await page.goto('/#/login');
  await page.getByRole('button', { name: '登录' }).click();

  await expect(page.getByRole('alert')).toBeVisible();
  await expect(page.getByText('请输入账号')).toBeVisible();
  await expect(page.getByText('请输入密码')).toBeVisible();
});

