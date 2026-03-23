const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
app.use(cors());
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true }));

// MySQL连接池
const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: 'root',
  database: 'homework_db',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// 保存作业
app.post('/saveHomework', async (req, res) => {
  try {
    const { content, isPermanent, attachmentUrl } = req.body;
    const connection = await pool.getConnection();
    
    await connection.execute(
      'INSERT INTO homework (content, isPermanent, attachmentUrl, createTime) VALUES (?, ?, ?, NOW())',
      [content, isPermanent ? 1 : 0, attachmentUrl || null]
    );
    
    connection.release();
    res.json({ success: true, message: '作业保存成功' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: '保存失败' });
  }
});

// 获取所有作业
app.get('/getHomework', async (req, res) => {
  try {
    const connection = await pool.getConnection();
    
    const [rows] = await connection.execute(
      'SELECT * FROM homework ORDER BY createTime DESC'
    );
    
    connection.release();
    res.json({ success: true, data: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: '查询失败' });
  }
});

// 定时清除非永久作业（每小时执行一次）
setInterval(async () => {
  try {
    const connection = await pool.getConnection();
    
    // 删除创建时间超过3个月且非永久的作业
    await connection.execute(
      'DELETE FROM homework WHERE isPermanent = 0 AND createTime < DATE_SUB(NOW(), INTERVAL 3 MONTH)'
    );
    
    connection.release();
    console.log('已清除过期作业');
  } catch (error) {
    console.error('清除作业失败:', error);
  }
}, 3600000); // 1小时

const PORT = 3001;
app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
});
