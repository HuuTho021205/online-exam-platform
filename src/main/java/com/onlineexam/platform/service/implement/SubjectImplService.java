package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.entity.Subject;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.SubjectRepository;
import com.onlineexam.platform.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubjectImplService implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getSubjectById(Long subjectId) {
       return subjectRepository.findById(subjectId)
                .orElseThrow(()-> new ResourceNotFoundException("Subject", "Subject Id", subjectId));
    }
}
