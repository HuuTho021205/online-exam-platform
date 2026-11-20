package com.onlineexam.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorRequest {
    @NotBlank(message = "Tên ngành không được để trống")
    @Size(max = 100, message = "Tên ngành không được vượt quá 100 ký tự")
    private String majorName;
    private List<Long> subjectIds;
}
