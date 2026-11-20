package com.onlineexam.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MajorResponse {
    private Long majorId;
    private String majorName;
    private List<String> subjectNames;
    private int classroomCount;
}
