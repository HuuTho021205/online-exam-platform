package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.QuestionRequest;
import com.onlineexam.platform.dto.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
    Page<QuestionResponse> getAllQuestion(String search, Pageable pageable, Long subjectId);
    QuestionRequest getQuestionById(Long questionId);
    void createQuestion(QuestionRequest questionRequest);
    void updateQuestion(Long questionId, QuestionRequest questionRequest);
    void deleteQuestion(Long questionId);
}
