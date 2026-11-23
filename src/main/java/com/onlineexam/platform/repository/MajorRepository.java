package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
    boolean existsByMajorNameIgnoreCase(String majorName);
    boolean existsByMajorNameIgnoreCaseAndMajorIdNot(String majorName, Long majorId);
    int countBySubjects_SubjectId(Long subjectId);
}
