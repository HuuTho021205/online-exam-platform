package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "select * from question as q where q.subject_id = :subjectId order by rand() limit :quantity", nativeQuery = true)
    List<Question> randomQuestion(@Param("quantity")int quantity, @Param("subjectId") Long subjectId);
}
