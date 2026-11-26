package com.onlineexam.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {
    private Long classId;
    private String className;
    private String majorName;
    private int studentCount;
}
