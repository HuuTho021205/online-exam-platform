package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    @Query("""
            SELECT COUNT(ea) > 0
            FROM ExamAttempt ea
            JOIN ExamQuestion eq ON ea.exam.examId = eq.exam.examId
            WHERE eq.question.questionId = :questionId
            """)
    boolean existsAttemptByQuestionId(@Param("questionId") Long questionId);
}