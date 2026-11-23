package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Exam;
import com.onlineexam.platform.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    int countBySubject_SubjectId(Long subjectSubjectId);
}
