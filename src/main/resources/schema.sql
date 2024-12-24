-- Create a dummy table for file database in H2
CREATE TABLE IF NOT EXISTS FILES (
                                     ID BIGINT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier for each file
                                     FILE_NAME VARCHAR(255) NOT NULL,      -- Name of the file
    FILE_TYPE VARCHAR(50),               -- Type of the file (e.g., txt, pdf, png)
    FILE_SIZE BIGINT,                    -- Size of the file in bytes
    UPLOAD_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp of file upload
    FILE_PATH VARCHAR(500) NOT NULL,     -- Path where the file is stored
    DESCRIPTION VARCHAR(500)             -- Optional description of the file
    );