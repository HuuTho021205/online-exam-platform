package com.onlineexam.platform.controller;

import com.onlineexam.platform.entity.Subject;
import com.onlineexam.platform.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("/subject")
    public String getAllSubject(Model model){
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects",subjects);
        return "subject/list";
    }
}
