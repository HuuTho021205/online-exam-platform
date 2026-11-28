package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM question q WHERE q.subject_id = :subjectId ORDER BY RAND() LIMIT :quantity", nativeQuery = true)
    List<Question> randomQuestion(@Param("quantity") int quantity, @Param("subjectId") Long subjectId);

    int countBySubject_SubjectId(Long subjectId);

    @Query(
            value = """
                    SELECT q FROM Question q
                    JOIN FETCH q.subject s
                    WHERE (:subjectId IS NULL OR s.subjectId = :subjectId)
                      AND (:keyword IS NULL OR :keyword = '' OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
                    ORDER BY q.questionId DESC
                    """,
            countQuery = """
                    SELECT COUNT(q) FROM Question q
                    WHERE (:subjectId IS NULL OR q.subject.subjectId = :subjectId)
                      AND (:keyword IS NULL OR :keyword = '' OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
                    """
    )
    Page<Question> searchQuestions(@Param("subjectId") Long subjectId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}