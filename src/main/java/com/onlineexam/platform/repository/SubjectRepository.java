package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
