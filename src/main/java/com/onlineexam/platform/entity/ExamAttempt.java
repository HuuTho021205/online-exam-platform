package com.onlineexam.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examAttemptId;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime deadlineAt;

    private LocalDateTime submittedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ExamAttemptStatus status = ExamAttemptStatus.IN_PROGRESS;

    private BigDecimal score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;
}
