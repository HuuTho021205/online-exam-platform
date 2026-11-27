package com.onlineexam.platform.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("message: {}", ex.getMessage());
        model.addAttribute("customMessage", "Bạn không có quyền truy cập vào đường dẫn này");
        return "error/403";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException ex, Model model){
        model.addAttribute("customMessage", "Không tìm thấy trang bạn yêu cầu");
        log.warn(ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model){
        model.addAttribute("customMessage", "Không tìm thấy tài nguyên");
        log.warn(ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(BusinessRuleException.class)
    public String handleBusinessRuleException(BusinessRuleException ex,
                                              HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        log.warn("Business rule violation at [{}]: {} - {}",
                request.getRequestURI(), ex.getMessage(), ex.getErrorMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getErrorMessage());

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex){
        log.error("Internal server error: ", ex);
        return "error/500";
    }

}