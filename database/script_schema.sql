CREATE DATABASE IF NOT EXISTS ThiTracNghiem
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ThiTracNghiem;

CREATE TABLE IF NOT EXISTS MonHoc (
                                      MaMonHoc INT AUTO_INCREMENT PRIMARY KEY,
                                      TenMonHoc VARCHAR(100) NOT NULL
    );

INSERT INTO MonHoc (TenMonHoc)
VALUES
    ('Lập trình Java'),
    ('Cơ sở dữ liệu'),
    ('Phát triển ứng dụng Web'),
    ('Mạng máy tính'),
    ('Kiến trúc máy tính');

SELECT * FROM MonHoc;