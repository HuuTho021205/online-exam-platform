package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.MajorRequest;
import com.onlineexam.platform.dto.MajorResponse;

import java.util.List;

public interface MajorService {
    List<MajorResponse> getAllMajors();
    MajorRequest getMajorById(Long majorId);
    void createMajor(MajorRequest majorRequest);
    void updateMajor(Long majorId, MajorRequest majorRequest);
    void deleteMajor(Long majorId);
}
