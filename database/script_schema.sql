CREATE DATABASE IF NOT EXISTS ThiTracNghiem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ThiTracNghiem;

DROP TABLE IF EXISTS attempt_answer;
DROP TABLE IF EXISTS exam_attempt;
DROP TABLE IF EXISTS exam_question;
DROP TABLE IF EXISTS exam_classroom;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS choice;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS classroom;
DROP TABLE IF EXISTS major_subject;
DROP TABLE IF EXISTS subject;
DROP TABLE IF EXISTS major;

CREATE TABLE major (
                       major_id    BIGINT          NOT NULL AUTO_INCREMENT,
                       major_name  VARCHAR(100) NOT NULL,
                       created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT pk_major PRIMARY KEY (major_id),
                       CONSTRAINT uq_major_name UNIQUE (major_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE subject (
                         subject_id    BIGINT          NOT NULL AUTO_INCREMENT,
                         subject_name  VARCHAR(100) NOT NULL,
                         created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT pk_subject PRIMARY KEY (subject_id),
                         CONSTRAINT uq_subject_name UNIQUE (subject_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE major_subject (
                               major_id    BIGINT NOT NULL,
                               subject_id  BIGINT NOT NULL,
                               CONSTRAINT pk_major_subject PRIMARY KEY (major_id, subject_id),
                               CONSTRAINT fk_major_subject_major
                                   FOREIGN KEY (major_id)   REFERENCES major (major_id)     ON DELETE CASCADE,
                               CONSTRAINT fk_major_subject_subject
                                   FOREIGN KEY (subject_id) REFERENCES subject (subject_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE classroom (
                           classroom_id  BIGINT         NOT NULL AUTO_INCREMENT,
                           major_id      BIGINT         NOT NULL,
                           class_name    VARCHAR(50) NOT NULL,
                           created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT pk_classroom PRIMARY KEY (classroom_id),
                           CONSTRAINT uq_classroom_name UNIQUE (class_name),
                           CONSTRAINT fk_classroom_major
                               FOREIGN KEY (major_id) REFERENCES major (major_id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE users (
                       user_id       BIGINT          NOT NULL AUTO_INCREMENT,
                       classroom_id  BIGINT          NULL,
                       full_name     VARCHAR(100) NOT NULL,
                       username      VARCHAR(50)  NOT NULL,
                       email         VARCHAR(100) NOT NULL,
                       password      VARCHAR(100) NOT NULL,
                       role          ENUM ('ADMIN', 'LECTURER', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
                       is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT pk_users PRIMARY KEY (user_id),
                       CONSTRAINT uq_users_username UNIQUE (username),
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT fk_users_classroom
                           FOREIGN KEY (classroom_id) REFERENCES classroom (classroom_id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE question (
                          question_id  BIGINT      NOT NULL AUTO_INCREMENT,
                          subject_id   BIGINT      NOT NULL,
                          content      TEXT     NOT NULL,
                          created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT pk_question PRIMARY KEY (question_id),
                          CONSTRAINT fk_question_subject
                              FOREIGN KEY (subject_id) REFERENCES subject (subject_id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE choice (
                        choice_id    BIGINT          NOT NULL AUTO_INCREMENT,
                        question_id  BIGINT          NOT NULL,
                        content      VARCHAR(500) NOT NULL,
                        is_correct   BOOLEAN      NOT NULL DEFAULT FALSE,
                        created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT pk_choice PRIMARY KEY (choice_id),
                        CONSTRAINT fk_choice_question
                            FOREIGN KEY (question_id) REFERENCES question (question_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE exam (
                      exam_id           BIGINT          NOT NULL AUTO_INCREMENT,
                      subject_id        BIGINT          NOT NULL,
                      exam_name         VARCHAR(150) NOT NULL,
                      start_time        DATETIME     NOT NULL,
                      end_time          DATETIME     NOT NULL,
                      duration_minutes  INT          NOT NULL,
                      created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      created_by        BIGINT          NOT NULL,
                      CONSTRAINT pk_exam PRIMARY KEY (exam_id),
                      CONSTRAINT fk_exam_subject
                          FOREIGN KEY (subject_id) REFERENCES subject (subject_id) ON DELETE RESTRICT,
                      CONSTRAINT fk_exam_created_by
                          FOREIGN KEY (created_by) REFERENCES users (user_id)      ON DELETE RESTRICT,
                      CONSTRAINT chk_exam_time_range CHECK (end_time > start_time),
                      CONSTRAINT chk_exam_duration   CHECK (duration_minutes > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE exam_classroom (
                                classroom_id  BIGINT NOT NULL,
                                exam_id       BIGINT NOT NULL,
                                CONSTRAINT pk_exam_classroom PRIMARY KEY (classroom_id, exam_id),
                                CONSTRAINT fk_exam_classroom_classroom
                                    FOREIGN KEY (classroom_id) REFERENCES classroom (classroom_id) ON DELETE CASCADE,
                                CONSTRAINT fk_exam_classroom_exam
                                    FOREIGN KEY (exam_id)      REFERENCES exam (exam_id)           ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE exam_question (
                               exam_question_id  BIGINT NOT NULL AUTO_INCREMENT,
                               exam_id           BIGINT NOT NULL,
                               question_id       BIGINT NOT NULL,
                               question_order    INT NOT NULL,
                               CONSTRAINT pk_exam_question PRIMARY KEY (exam_question_id),
                               CONSTRAINT uq_exam_question_order    UNIQUE (exam_id, question_order),
                               CONSTRAINT uq_exam_question_question UNIQUE (exam_id, question_id),
                               CONSTRAINT fk_exam_question_exam
                                   FOREIGN KEY (exam_id)     REFERENCES exam (exam_id)         ON DELETE CASCADE,
                               CONSTRAINT fk_exam_question_question
                                   FOREIGN KEY (question_id) REFERENCES question (question_id) ON DELETE RESTRICT,
                               CONSTRAINT chk_exam_question_order CHECK (question_order > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE exam_attempt (
                              exam_attempt_id  BIGINT          NOT NULL AUTO_INCREMENT,
                              user_id          BIGINT          NOT NULL,
                              exam_id          BIGINT          NOT NULL,
                              started_at       DATETIME     NOT NULL,
                              deadline_at      DATETIME     NOT NULL,
                              submitted_at     DATETIME     NULL,
                              status           ENUM ('IN_PROGRESS', 'SUBMITTED') NOT NULL DEFAULT 'IN_PROGRESS',
                              score            DECIMAL(4,2) NULL,
                              CONSTRAINT pk_exam_attempt PRIMARY KEY (exam_attempt_id),
                              CONSTRAINT uq_exam_attempt_user_exam UNIQUE (user_id, exam_id),
                              CONSTRAINT fk_exam_attempt_user
                                  FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
                              CONSTRAINT fk_exam_attempt_exam
                                  FOREIGN KEY (exam_id) REFERENCES exam (exam_id)  ON DELETE RESTRICT,
                              CONSTRAINT chk_attempt_deadline CHECK (deadline_at > started_at),
                              CONSTRAINT chk_attempt_score    CHECK (score IS NULL OR (score >= 0 AND score <= 10)),
                              CONSTRAINT chk_attempt_status_submitted CHECK (
                                  (status = 'SUBMITTED'   AND submitted_at IS NOT NULL) OR
                                  (status = 'IN_PROGRESS' AND submitted_at IS NULL)
                                  )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE attempt_answer (
                                attempt_answer_id  BIGINT      NOT NULL AUTO_INCREMENT,
                                exam_attempt_id    BIGINT      NOT NULL,
                                question_id        BIGINT      NOT NULL,
                                choice_id          BIGINT      NULL,
                                created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT pk_attempt_answer PRIMARY KEY (attempt_answer_id),
                                CONSTRAINT uq_attempt_answer_attempt_question UNIQUE (exam_attempt_id, question_id),
                                CONSTRAINT fk_attempt_answer_attempt
                                    FOREIGN KEY (exam_attempt_id) REFERENCES exam_attempt (exam_attempt_id) ON DELETE CASCADE,
                                CONSTRAINT fk_attempt_answer_question
                                    FOREIGN KEY (question_id)     REFERENCES question (question_id)         ON DELETE RESTRICT,
                                CONSTRAINT fk_attempt_answer_choice
                                    FOREIGN KEY (choice_id)       REFERENCES choice (choice_id)             ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;