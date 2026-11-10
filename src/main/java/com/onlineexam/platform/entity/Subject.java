package com.onlineexam.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subjectId;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String subjectName;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "subject")
    private List<Question> questions = new ArrayList<>();
}
