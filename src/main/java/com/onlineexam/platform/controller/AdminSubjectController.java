package com.onlineexam.platform.controller;

import com.onlineexam.platform.dto.SubjectRequest;
import com.onlineexam.platform.dto.SubjectResponse;
import com.onlineexam.platform.exception.BusinessRuleException;
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

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController {
    private final SubjectService subjectService;
    private static final String LIST_VIEW = "admin/subjects/list";
    private static final String FORM_VIEW = "admin/subjects/form";
    private static final String LIST_REDIRECT = "redirect:/admin/subjects";

    @GetMapping
    public String getAllSubjects(Model model){
        List<SubjectResponse> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects",subjects);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("subjectRequest", new SubjectRequest());
        prepareForm(model,"Thêm môn học","/admin/subjects");
        return FORM_VIEW;
    }

    @PostMapping()
    public String createSubject(@Valid @ModelAttribute("subjectRequest") SubjectRequest form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes){
        if (!bindingResult.hasErrors()){
            try {
                subjectService.createSubject(form);
                redirectAttributes.addFlashAttribute("successMessage","Thêm môn học thành công");
                return LIST_REDIRECT;
            }
            catch (BusinessRuleException ex){
                log.warn("Create subject rejected: {}", ex.getMessage());
                bindingResult.rejectValue("subjectName", "subject.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model,"Thêm môn học", "/admin/subjects");
        return FORM_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        model.addAttribute("subjectRequest", subjectService.getSubjectById(id));
        prepareForm(model,"Sửa môn học","/admin/subjects/" + id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String updateSubject(@PathVariable Long id, @Valid @ModelAttribute("subjectRequest") SubjectRequest subjectRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model){
        if (!bindingResult.hasErrors()){
            try {
                subjectService.updateSubject(id,subjectRequest);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật môn học thành công");
                return LIST_REDIRECT;
            }catch (BusinessRuleException ex){
                log.warn("Update subject rejected: {}", ex.getMessage());
                bindingResult.rejectValue("subjectName", "subject.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model,"Sửa môn học","/admin/subjects/" + id);
        return FORM_VIEW;
    }
    @PostMapping("/{id}/delete")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            subjectService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa môn học thành công");
        }catch (BusinessRuleException ex){
            log.warn("Delete subject rejected: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getErrorMessage());
        }
        return LIST_REDIRECT;
    }
    private void prepareForm(Model model, String pageTitle, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
    }
}
