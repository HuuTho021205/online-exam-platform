CREATE DATABASE IF NOT EXISTS ThiTracNghiem
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ThiTracNghiem;

CREATE TABLE IF NOT EXISTS Subject (
                                      subject_id INT AUTO_INCREMENT PRIMARY KEY,
                                      subject_name VARCHAR(100) NOT NULL
    );

INSERT INTO subject (subject_name)
VALUES
    ('Lập trình Java'),
    ('Cơ sở dữ liệu'),
    ('Phát triển ứng dụng Web'),
    ('Mạng máy tính'),
    ('Kiến trúc máy tính');

SELECT * FROM subject;