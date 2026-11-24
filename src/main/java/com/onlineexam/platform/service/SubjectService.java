package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.SubjectRequest;
import com.onlineexam.platform.dto.SubjectResponse;

import java.util.List;

public interface SubjectService {
    List<SubjectResponse> getAllSubjects();
    void createSubject(SubjectRequest subjectRequest);
    void updateSubject(Long id, SubjectRequest subjectRequest);
    void deleteSubject(Long id);
    SubjectRequest getSubjectById(Long subjectId);
}
