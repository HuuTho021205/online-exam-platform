package com.onlineexam.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long questionId;
    private String content;
    private String subjectName;
    private boolean inExam;
    private boolean hasAttempts;
}