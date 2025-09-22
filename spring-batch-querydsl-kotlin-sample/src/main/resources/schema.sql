DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS student_address;
CREATE TABLE student
(
    seq_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    age   INT          NOT NULL,
    is_processed BOOLEAN NOT NULL
);

CREATE TABLE student_address
(
    seq_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_seq_no BIGINT NOT NULL,
    address  VARCHAR(255) NOT NULL,
    FOREIGN KEY (student_seq_no) REFERENCES student (seq_no)
);
