package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.ClassroomRequest;
import com.onlineexam.platform.dto.ClassroomResponse;

import java.util.List;

public interface ClassroomService {
    void createClassroom(ClassroomRequest classroomRequest);
    void updateClassroom(Long classroomId, ClassroomRequest classroomRequest);
    List<ClassroomResponse> getAllClassrooms();
    void deleteClassroom(Long classroomId);
    ClassroomRequest getClassroomById(Long classroomId);
}
