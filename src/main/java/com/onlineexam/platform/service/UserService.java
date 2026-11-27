package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.UserCreateRequest;
import com.onlineexam.platform.dto.UserResponse;
import com.onlineexam.platform.dto.UserUpdateRequest;
import com.onlineexam.platform.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserResponse> getAllUsers(UserRole role, Pageable pageable);
    void toggleUserStatus(Long userId, String currentUsername);
    UserUpdateRequest getUserById(Long id);
    void createUser(UserCreateRequest userCreateRequest);
    void updateUser(Long id, UserUpdateRequest userUpdateRequest, String currentUserName);
}
