# 作业提交持久化系统 - 运行指南

## 项目结构
```
homework-server/
├── package.json          # 后端依赖配置
├── server.js            # Express服务器
└── init.sql             # 数据库建表脚本

client_code/src/views/pages/
└── homework.vue         # 前端页面
```

## 数据库配置

### 1. 创建数据库和表
在MySQL中执行 `homework-server/init.sql`：

```bash
mysql -u root -p < init.sql
```

或在MySQL客户端中直接执行文件内容。

**表结构说明：**
- `id`: 主键，自增
- `content`: 作业内容（长文本）
- `isPermanent`: 是否永久保存（0=否，1=是）
- `attachmentUrl`: 附件地址
- `createTime`: 创建时间（自动记录）

## 后端配置

### 1. 安装依赖
```bash
cd homework-server
npm install
```

### 2. 修改MySQL连接配置
编辑 `server.js` 第12-19行，根据你的MySQL配置修改：
```javascript
const pool = mysql.createPool({
  host: 'localhost',      // MySQL主机
  user: 'root',           // MySQL用户名
  password: 'root',       // MySQL密码
  database: 'homework_db' // 数据库名
});
```

### 3. 启动服务器
```bash
npm start
```

服务器将在 `http://localhost:3001` 运行

**API接口：**
- `POST /saveHomework` - 保存作业
  - 请求体: `{ content, isPermanent, attachmentUrl }`
  - 响应: `{ success, message }`

- `GET /getHomework` - 获取所有作业
  - 响应: `{ success, data: [...] }`

**自动清理：**
- 每小时执行一次，删除创建时间超过3个月且非永久的作业

## 前端配置

### 1. 添加路由
编辑 `client_code/src/router/index.js`，添加作业页面路由：
```javascript
{
  path: '/homework',
  name: 'Homework',
  component: () => import('@/views/pages/homework.vue')
}
```

### 2. 启动前端
```bash
cd client_code
npm run serve
```

前端将在 `http://localhost:8080` 运行

### 3. 访问作业页面
在浏览器中访问 `http://localhost:8080/#/homework`

## 功能说明

### 前端功能
1. **作业内容输入** - textarea输入框
2. **附件地址** - 支持输入URL或文件路径
3. **永久保存** - 复选框，勾选则作业永久保存
4. **提交按钮** - 提交作业到后端
5. **作业列表** - 显示所有已提交的作业，按时间倒序排列

### 后端功能
1. **保存作业** - 接收前端数据，存入数据库
2. **查询作业** - 返回所有作业列表
3. **自动清理** - 每小时清除过期的非永久作业

## 完整运行步骤

### 第一次运行
1. 在MySQL中执行 `init.sql` 创建数据库和表
2. 修改 `server.js` 中的MySQL连接配置
3. 启动后端：`cd homework-server && npm install && npm start`
4. 启动前端：`cd client_code && npm run serve`
5. 在浏览器访问 `http://localhost:8080/#/homework`

### 后续运行
1. 启动后端：`cd homework-server && npm start`
2. 启动前端：`cd client_code && npm run serve`
3. 访问 `http://localhost:8080/#/homework`

## 测试

### 测试提交作业
1. 输入作业内容
2. 输入附件地址（可选）
3. 勾选"是否永久保存"（可选）
4. 点击"提交作业"
5. 页面应显示成功提示，作业列表自动刷新

### 测试自动清理
- 非永久作业在创建后3个月自动删除
- 永久作业永不删除

## 常见问题

### 连接失败
- 检查MySQL是否运行
- 检查用户名、密码、数据库名是否正确
- 检查防火墙是否阻止3001端口

### 前端无法连接后端
- 检查后端是否启动在3001端口
- 检查浏览器控制台是否有CORS错误
- 确保后端已启用CORS中间件

### 数据库表不存在
- 确保执行了 `init.sql`
- 检查数据库名是否为 `homework_db`
