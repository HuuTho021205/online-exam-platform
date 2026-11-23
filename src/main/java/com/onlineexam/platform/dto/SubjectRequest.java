package com.onlineexam.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubjectRequest {
    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 100, message = "Tên môn học không được vượt quá 100 ký tự")
    private String subjectName;
}
