const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Database Connection
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'kh6479738888',
    database: 'bookon_db'
});

db.connect((err) => {
    if (err) {
        console.error('Database connection failed: ' + err.stack);
        return;
    }
    console.log('Connected to MySQL database.');
});

const PORT = 3000; // Use 3000 instead of 3306 to avoid conflict with MySQL
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
