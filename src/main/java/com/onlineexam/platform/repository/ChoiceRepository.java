package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByQuestion_QuestionIdOrderByChoiceIdAsc(Long questionId);
}
