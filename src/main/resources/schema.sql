-- Create a dummy table for file database in H2
CREATE TABLE IF NOT EXISTS HoyoAccount (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hoyo_uid VARCHAR(50),
    hoyo_name VARCHAR(50),
    account_name VARCHAR(50),
    type VARCHAR(50),
    code_redeem BOOLEAN,
    discord_id VARCHAR(50),
    cookie VARCHAR
);

CREATE TABLE IF NOT EXISTS SkportAccount (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cred VARCHAR(50),
    sk_game_role VARCHAR(50),
    owner_name VARCHAR(50),
    owner_discord_id VARCHAR(50)
);