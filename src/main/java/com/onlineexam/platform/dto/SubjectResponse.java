package com.onlineexam.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {
    private Long subjectId;
    private String subjectName;
    private int examCount;
    private int questionCount;
    private int majorCount;
}
