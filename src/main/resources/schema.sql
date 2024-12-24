-- Create a dummy table for file database in H2
CREATE TABLE IF NOT EXISTS account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(50),
    type VARCHAR(50),
    code_redeem BOOLEAN,
    cookie VARCHAR
);