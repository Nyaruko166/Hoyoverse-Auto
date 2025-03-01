-- Create a dummy table for file database in H2
CREATE TABLE IF NOT EXISTS account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hoyo_uid VARCHAR(50),
    hoyo_name VARCHAR(50),
    account_name VARCHAR(50),
    type VARCHAR(50),
    code_redeem BOOLEAN,
    discord_id VARCHAR(50),
    cookie VARCHAR
);