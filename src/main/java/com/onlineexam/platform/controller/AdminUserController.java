package com.onlineexam.platform.controller;

import com.onlineexam.platform.config.AppConstant;
import com.onlineexam.platform.dto.UserCreateRequest;
import com.onlineexam.platform.dto.UserResponse;
import com.onlineexam.platform.dto.UserUpdateRequest;
import com.onlineexam.platform.entity.UserRole;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.service.ClassroomService;
import com.onlineexam.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final ClassroomService classroomService;

    private static final String LIST_VIEW = "admin/users/list";
    private static final String FORM_VIEW = "admin/users/form";
    private static final String LIST_REDIRECT = "redirect:/admin/users";

    @GetMapping
    public String list(@RequestParam(required = false) UserRole role,
                       @RequestParam(defaultValue = AppConstant.PAGE_NUMBER) int page,
                       @RequestParam(defaultValue = AppConstant.PAGE_SIZE) int size,
                       @RequestParam(defaultValue = AppConstant.SORT_ORDER) String sortOrder,
                       Model model) {

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by("userId").ascending()
                : Sort.by("userId").descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> userPage = userService.getAllUsers(role, pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("startIndex", userPage.getNumber() * userPage.getSize());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("selectedRole", role);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userRequest", new UserCreateRequest());
        prepareForm(model, "Thêm người dùng", "/admin/users", false);
        return FORM_VIEW;
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userRequest") UserCreateRequest form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                userService.createUser(form);
                redirectAttributes.addFlashAttribute("successMessage", "Tạo người dùng thành công");
                return LIST_REDIRECT;
            } catch (BusinessRuleException ex) {
                log.warn("Create user rejected: {}", ex.getMessage());
                model.addAttribute("errorMessage", ex.getErrorMessage());
            }
        }
        prepareForm(model, "Thêm người dùng", "/admin/users", false);
        return FORM_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("userRequest", userService.getUserById(id));
        prepareForm(model, "Cập nhật người dùng", "/admin/users/" + id, true);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userRequest") UserUpdateRequest form,
                             BindingResult bindingResult,
                             Principal principal,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                userService.updateUser(id, form, principal.getName());
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công");
                return LIST_REDIRECT;
            } catch (BusinessRuleException ex) {
                log.warn("Update user rejected: {}", ex.getMessage());
                model.addAttribute("errorMessage", ex.getErrorMessage());
            }
        }
        prepareForm(model, "Cập nhật người dùng", "/admin/users/" + id, true);
        return FORM_VIEW;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái tài khoản thành công");
        } catch (BusinessRuleException ex) {
            log.warn("Toggle user status rejected: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getErrorMessage());
        }
        return LIST_REDIRECT;
    }

    private void prepareForm(Model model, String pageTitle, String actionForm, boolean isEdit) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("actionForm", actionForm);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
    }
}