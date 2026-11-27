package com.onlineexam.platform.service;

import com.onlineexam.platform.dto.UserCreateRequest;
import com.onlineexam.platform.dto.UserResponse;
import com.onlineexam.platform.dto.UserUpdateRequest;
import com.onlineexam.platform.entity.UserRole;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers(UserRole role);
    UserUpdateRequest getUserById(Long id);
    void createUser(UserCreateRequest userCreateRequest);
    void updateUser(Long id, UserUpdateRequest userUpdateRequest, String currentUserName);
    void deleteUser(Long id, String currentUserName);
}
