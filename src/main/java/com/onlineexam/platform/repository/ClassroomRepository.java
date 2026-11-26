package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    int countByMajor_MajorId(Long majorId);

    boolean existsByClassNameIgnoreCase(String className);
    boolean existsByClassNameIgnoreCaseAndClassroomIdNot(String className, Long classroomId);
}
