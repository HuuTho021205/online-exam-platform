package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {
}
