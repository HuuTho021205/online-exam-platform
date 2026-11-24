package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.dto.SubjectRequest;
import com.onlineexam.platform.dto.SubjectResponse;
import com.onlineexam.platform.entity.Subject;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.ExamRepository;
import com.onlineexam.platform.repository.MajorRepository;
import com.onlineexam.platform.repository.QuestionRepository;
import com.onlineexam.platform.repository.SubjectRepository;
import com.onlineexam.platform.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final MajorRepository majorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> new SubjectResponse(
                        subject.getSubjectId(),
                        subject.getSubjectName(),
                        examRepository.countBySubject_SubjectId(subject.getSubjectId()),
                        questionRepository.countBySubject_SubjectId(subject.getSubjectId()),
                        majorRepository.countBySubjects_SubjectId(subject.getSubjectId())))
                .toList();
    }

    @Override
    @Transactional
    public void createSubject(SubjectRequest subjectRequest) {
        String subjectName = subjectRequest.getSubjectName().strip();
        if (subjectRepository.existsBySubjectNameIgnoreCase(subjectName)) {
            throw new BusinessRuleException("Subject name already exists: " + subjectName, "Môn học đã tồn tại");
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void updateSubject(Long subjectId, SubjectRequest subjectRequest) {
        String subjectName = subjectRequest.getSubjectName().strip();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new ResourceNotFoundException("Subject", "Subject Id", subjectId));
        if (subjectRepository.existsBySubjectNameIgnoreCaseAndSubjectIdNot(subjectName, subjectId)) {
            throw new BusinessRuleException("Subject name already exists: " + subjectName, "Môn học đã tồn tại");
        }
        subject.setSubjectName(subjectName);
        subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new ResourceNotFoundException("Subject", "Subject Id", subjectId));
        if (examRepository.countBySubject_SubjectId(subjectId) > 0) {
            throw new BusinessRuleException("Subject is used in exams", "Môn học đang được sử dụng trong các bài thi");
        }
        else if (questionRepository.countBySubject_SubjectId(subjectId) > 0) {
            throw new BusinessRuleException("Subject is used in questions", "Môn học đang được sử dụng trong các câu hỏi");
        }
        subjectRepository.delete(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectRequest getSubjectById(Long subjectId) {
       Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new ResourceNotFoundException("Subject", "Subject Id", subjectId));
        return new SubjectRequest(subject.getSubjectName());
    }
}
