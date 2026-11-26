package com.onlineexam.platform.controller;

import com.onlineexam.platform.dto.ClassroomRequest;
import com.onlineexam.platform.dto.ClassroomResponse;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.service.ClassroomService;
import com.onlineexam.platform.service.MajorService;
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
@Controller
@RequestMapping("/admin/classrooms")
@RequiredArgsConstructor
public class AdminClassroomController {
    private final ClassroomService classroomService;
    private final MajorService majorService;
    private static final String LIST_VIEW = "admin/classrooms/list";
    private static final String FORM_VIEW = "admin/classrooms/form";
    private static final String LIST_REDIRECT = "redirect:/admin/classrooms";

    @GetMapping
    public String list (Model model){
        List<ClassroomResponse> classrooms = classroomService.getAllClassrooms();
        model.addAttribute("classrooms", classrooms);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("classroomRequest", new ClassroomRequest());
        prepareForm(model, "Thêm Lớp Học", "/admin/classrooms");
        return FORM_VIEW;
    }
    @PostMapping
    public String createClassroom(@Valid @ModelAttribute("classroomRequest") ClassroomRequest form,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes){
        if(!bindingResult.hasErrors()){
            try {
                classroomService.createClassroom(form);
                redirectAttributes.addFlashAttribute("successMessage","Đã thêm lớp học thành công");
                return LIST_REDIRECT;
            }catch (BusinessRuleException ex){
                log.warn("created classroom rejected {}", ex.getMessage());
                bindingResult.rejectValue("className", "classroom.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model, "Thêm lớp học", "/admin/classrooms");
        return FORM_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(Model model, @PathVariable Long id){
        model.addAttribute("classroomRequest", classroomService.getClassroomById(id));
        prepareForm(model,"Sửa lớp học", "/admin/classrooms/" + id);
        return FORM_VIEW;
    }
    @PostMapping("/{id}")
    public String updateClassroom(Model model, @PathVariable Long id,
                                  @Valid @ModelAttribute("classroomRequest") ClassroomRequest form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes){
        if (!bindingResult.hasErrors()){
            try {
                classroomService.updateClassroom(id, form);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lớp học thành công");
                return LIST_REDIRECT;
            }catch (BusinessRuleException ex){
                log.warn("updated classroom rejected {}", ex.getMessage());
                bindingResult.rejectValue("className", "classroom.rule", ex.getErrorMessage());
            }
        }
        prepareForm(model,"Sửa lớp học", "/admin/classrooms/" + id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}/delete")
    public String deleteClassroom(Model model, @PathVariable Long id,
                                  RedirectAttributes redirectAttributes){
        try {
            classroomService.deleteClassroom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa lớp học thành công");
        }catch (BusinessRuleException ex){
            log.warn("deleted classroom rejected {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getErrorMessage());
        }
        return LIST_REDIRECT;
    }

    private void prepareForm(Model model, String pageTitle, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("majors", majorService.getAllMajors());
    }
}
