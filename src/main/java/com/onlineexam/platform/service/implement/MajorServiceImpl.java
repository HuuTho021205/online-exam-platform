package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.dto.MajorRequest;
import com.onlineexam.platform.dto.MajorResponse;
import com.onlineexam.platform.entity.Major;
import com.onlineexam.platform.entity.Subject;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.ClassroomRepository;
import com.onlineexam.platform.repository.MajorRepository;
import com.onlineexam.platform.repository.SubjectRepository;
import com.onlineexam.platform.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final MajorRepository majorRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MajorResponse> getAllMajors() {
        return majorRepository.findAll().stream()
                .map(major -> new MajorResponse(
                        major.getMajorId(),
                        major.getMajorName(),
                        major.getSubjects().stream()
                                .map(Subject::getSubjectName)
                                .toList(),
                        classroomRepository.countByMajor_MajorId(major.getMajorId())
                        ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MajorRequest getMajorById(Long majorId) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new ResourceNotFoundException("Major","MajorId",majorId));
        List<Long> subjectIds = major.getSubjects().stream()
                .map(Subject::getSubjectId)
                .toList();
        return new MajorRequest(major.getMajorName(), subjectIds);
    }

    @Override
    @Transactional
    public void createMajor(MajorRequest majorRequest) {
        String majorName = majorRequest.getMajorName().strip();
        if(majorRepository.existsByMajorNameIgnoreCase(majorName)){
            throw new BusinessRuleException("Major name already exists:" + majorName
                    ,"Tên ngành đã tồn tại");
        }
        Major major = new Major();
        major.setMajorName(majorName);
        List<Subject> subjects = resolveSubjects(majorRequest.getSubjectIds());
        major.setSubjects(subjects);
        majorRepository.save(major);
    }

    @Override
    @Transactional
    public void updateMajor(Long majorId, MajorRequest majorRequest) {
        String majorName = majorRequest.getMajorName().strip();
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new ResourceNotFoundException("Major","MajorId",majorId));
        if (majorRepository.existsByMajorNameIgnoreCaseAndMajorIdNot(majorName,majorId)){
            throw new BusinessRuleException("Major name already exists:" + majorName
                    ,"Tên ngành đã tồn tại");
        }
        List<Subject> subjects = resolveSubjects(majorRequest.getSubjectIds());
        major.setMajorName(majorName);
        major.setSubjects(subjects);
        majorRepository.save(major);
    }

    @Override
    @Transactional
    public void deleteMajor(Long majorId) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new ResourceNotFoundException("Major","MajorId",majorId));
        if (classroomRepository.countByMajor_MajorId(majorId) > 0){
            throw new BusinessRuleException("Major has classrooms","Ngành có lớp học");
        }
        majorRepository.delete(major);
    }
    // Hàm dùng chung để lấy và kiểm tra danh sách môn học
    private List<Subject> resolveSubjects(List<Long> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> uniqueIds = subjectIds.stream().distinct().toList();
        List<Subject> subjects = subjectRepository.findAllById(uniqueIds);
        if (subjects.size() != uniqueIds.size()) {
            List<Long> foundIds = subjects.stream()
                    .map(Subject::getSubjectId)
                    .toList();
            List<Long> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Subject", "subjectIds", missingIds);
        }

        return subjects;
    }
}
