package com.onlineexam.platform.controller;

import com.onlineexam.platform.repository.ClassroomRepository;
import com.onlineexam.platform.repository.MajorRepository;
import com.onlineexam.platform.repository.SubjectRepository;
import com.onlineexam.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalMajors", majorRepository.count());
        model.addAttribute("totalClassrooms", classroomRepository.count());
        model.addAttribute("totalSubjects", subjectRepository.count());
        return "admin/dashboard";
    }
}