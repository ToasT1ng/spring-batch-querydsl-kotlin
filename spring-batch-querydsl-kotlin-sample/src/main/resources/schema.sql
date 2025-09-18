DROP TABLE IF EXISTS student;
CREATE TABLE student
(
    seq_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    age   INT          NOT NULL,
    is_processed BOOLEAN NOT NULL
);