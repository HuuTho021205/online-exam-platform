package com.onlineexam.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lecturer")
public class LecturerDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "lecturer/dashboard";
    }
}
