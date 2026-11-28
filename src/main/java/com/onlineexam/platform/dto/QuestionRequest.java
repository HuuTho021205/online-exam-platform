package com.onlineexam.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    @Size(min = 5, max = 2000, message = "Nội dung câu hỏi phải từ 5 đến 2000 ký tự")
    private String content;

    @NotNull(message = "Danh sách đáp án không được để trống")
    @Size(min = 4, max = 4, message = "Câu hỏi phải có đúng 4 đáp án")
    private List<@NotBlank(message = "Nội dung từng đáp án không được để trống")
    @Size(max = 500, message = "Nội dung đáp án tối đa 500 ký tự") String> options;

    @NotBlank(message = "Đáp án đúng không được để trống")
    @Pattern(regexp = "^[A-D]$", message = "Đáp án đúng phải là A, B, C hoặc D")
    private String correctOption;

    @NotNull(message = "Môn học không được để trống")
    private Long subjectId;

    private boolean inExam = false;
    private boolean hasAttempts = false;

    public QuestionRequest() {
        this.options = new ArrayList<>(Arrays.asList("", "", "", ""));
    }
}