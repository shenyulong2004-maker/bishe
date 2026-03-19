## 登录页 UI 自动化测试报告

### 覆盖范围
- 响应式显示一致性（桌面端/移动端，无横向溢出）
- 键盘可达性（Tab 聚焦顺序）
- 表单可用性（空提交错误提示可见且可读）

### 用例位置
- client_code：
  - d:\bishe\client_code\tests\login-ui.spec.js
  - d:\bishe\client_code\playwright.config.js
- manage_code：
  - d:\bishe\manage_code\tests\login-ui.spec.js
  - d:\bishe\manage_code\playwright.config.js

### 运行方式
#### 1) 安装浏览器（仅首次）
- client_code：
  - npm run test:e2e:install
- manage_code：
  - npm run test:e2e:install

#### 2) 启动前端（另开终端）
- client_code：
  - npm run serve
- manage_code：
  - npm run serve

#### 3) 执行测试
- 默认 baseURL 为 http://localhost:8080
- 若端口不同，设置环境变量：
  - Windows PowerShell：$env:PLAYWRIGHT_BASE_URL="http://localhost:8081"
- 然后运行：
  - npm run test:e2e

### 用例列表与预期
#### client_code
- 桌面端布局可用且无横向溢出：通过
- 移动端布局可用且无横向溢出：通过
- 键盘可达（Tab 顺序覆盖账号/密码/用户类型/记住密码/登录）：通过
- 空提交错误提示：通过

#### manage_code
- 桌面端布局可用且无横向溢出：通过
- 移动端布局可用且无横向溢出：通过
- 键盘可达（Tab 顺序覆盖账号/密码/登录）：通过
- 空提交错误提示：通过

### 备注
- client_code 用例通过 addInitScript 写入 localStorage.menus，避免依赖后端菜单接口，以保证 UI 测试稳定性。
- manage_code 登录成功流程包含拼图验证码，当前自动化用例覆盖 UI 与表单验证，不强行完成验证码交互。

