package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
