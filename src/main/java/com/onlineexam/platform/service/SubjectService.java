package com.onlineexam.platform.service;

import com.onlineexam.platform.entity.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> getAllSubjects();

    Subject getSubjectById(Long subjectId);
}
