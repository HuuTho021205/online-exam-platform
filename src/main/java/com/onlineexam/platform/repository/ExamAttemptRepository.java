package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
}
