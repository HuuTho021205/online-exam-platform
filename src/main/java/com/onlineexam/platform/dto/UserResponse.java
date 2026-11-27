package com.onlineexam.platform.dto;

import com.onlineexam.platform.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String classroomName;
    private boolean active;
}
