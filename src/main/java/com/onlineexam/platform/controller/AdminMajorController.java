package com.onlineexam.platform.controller;

import com.onlineexam.platform.dto.MajorRequest;
import com.onlineexam.platform.dto.MajorResponse;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.service.MajorService;
import com.onlineexam.platform.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/majors")
@RequiredArgsConstructor
@Slf4j
public class AdminMajorController {
    private final MajorService majorService;
    private final SubjectService subjectService;
    private static final String LIST_VIEW = "admin/majors/list";
    private static final String FORM_VIEW = "admin/majors/form";
    private static final String LIST_REDIRECT = "redirect:/admin/majors";

    @GetMapping
    public String list(Model model) {
        List<MajorResponse> majors = majorService.getAllMajors();
        model.addAttribute("majors", majors);
        return LIST_VIEW;
    }

    @GetMapping("/new") // 1. Đã bổ sung annotation bị thiếu
    public String showCreateForm(Model model) {
        model.addAttribute("majorRequest", new MajorRequest()); // 2. Đổi majorForm -> majorRequest
        prepareForm(model, "Thêm ngành học", "/admin/majors");
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("majorRequest") MajorRequest form, // 2. Đổi majorForm -> majorRequest
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                majorService.createMajor(form);
                redirectAttributes.addFlashAttribute("successMessage", "Đã thêm ngành học thành công");
                return LIST_REDIRECT;
            } catch (BusinessRuleException ex) {
                log.warn("Create major rejected: {}", ex.getMessage());
                bindingResult.rejectValue("majorName", "major.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model, "Thêm ngành học", "/admin/majors");
        return FORM_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("majorRequest", majorService.getMajorById(id)); // 2. Đổi majorForm -> majorRequest
        prepareForm(model, "Sửa ngành học", "/admin/majors/" + id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String updateMajor(@PathVariable Long id,
                              @Valid @ModelAttribute("majorRequest") MajorRequest majorRequest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                majorService.updateMajor(id, majorRequest);
                redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ngành học thành công");
                return LIST_REDIRECT;
            } catch (BusinessRuleException ex) {
                log.warn("Update major {} rejected: {}", id, ex.getMessage());
                bindingResult.rejectValue("majorName", "major.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model, "Sửa ngành học", "/admin/majors/" + id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}/delete")
    public String deleteMajor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            majorService.deleteMajor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa ngành học");
        } catch (BusinessRuleException ex) {
            log.warn("Delete major {} rejected: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getErrorMessage());
        }
        return LIST_REDIRECT;
    }

    private void prepareForm(Model model, String pageTitle, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("subjects", subjectService.getAllSubjects()); // 3. Đổi allSubjects -> subjects
    }
}