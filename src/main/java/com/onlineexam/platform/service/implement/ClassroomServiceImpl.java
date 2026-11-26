package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.dto.ClassroomRequest;
import com.onlineexam.platform.dto.ClassroomResponse;
import com.onlineexam.platform.entity.Classroom;
import com.onlineexam.platform.entity.Major;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.ClassroomRepository;
import com.onlineexam.platform.repository.MajorRepository;
import com.onlineexam.platform.repository.UserRepository;
import com.onlineexam.platform.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getAllClassrooms() {
        List<Classroom> classrooms = classroomRepository.findAll();
        return classrooms.stream().map(
                classroom -> new ClassroomResponse(
                        classroom.getClassroomId(),
                        classroom.getClassName(),
                        classroom.getMajor().getMajorName(),
                        userRepository.countByClassroom_ClassroomId(classroom.getClassroomId())
                )
        ).toList();
    }

    @Override
    @Transactional
    public void createClassroom(ClassroomRequest classroomRequest) {
        String className = classroomRequest.getClassName().strip();
        if (classroomRepository.existsByClassNameIgnoreCase(className)){
            throw new BusinessRuleException("Classroom name already exists: " + className, "Tên lớp đã tồn tại");
        }
        Major major = majorRepository.findById(classroomRequest.getMajorId())
                .orElseThrow(()-> new ResourceNotFoundException("Major","MajorId", classroomRequest.getMajorId()));
        Classroom classroom = new Classroom();
        classroom.setClassName(className);
        classroom.setMajor(major);
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void updateClassroom(Long classroomId, ClassroomRequest classroomRequest) {
        Major major = majorRepository.findById(classroomRequest.getMajorId())
                .orElseThrow(()-> new ResourceNotFoundException("Major","MajorId", classroomRequest.getMajorId()));
        String className = classroomRequest.getClassName().strip();
        if(classroomRepository.existsByClassNameIgnoreCaseAndClassroomIdNot(className, classroomId)){
            throw new BusinessRuleException("Classroom name already exists: " + className, "Tên lớp đã tồn tại");
        }
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(()-> new ResourceNotFoundException("Classroom","ClassroomId", classroomId));
        classroom.setClassName(className);
        classroom.setMajor(major);
        classroomRepository.save(classroom);

    }

    @Override
    @Transactional
    public void deleteClassroom(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(()-> new ResourceNotFoundException("Classroom","ClassroomId", classroomId));
        if (userRepository.countByClassroom_ClassroomId(classroomId) > 0){
            throw new BusinessRuleException("Classroom is used by user", "Không thể xóa lớp học đang có sinh viên");
        }
        classroomRepository.delete(classroom);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassroomRequest getClassroomById(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(()-> new ResourceNotFoundException("Classroom","ClassroomId", classroomId));

        return new ClassroomRequest(classroom.getClassName(), classroom.getMajor().getMajorId()) ;
    }
}
