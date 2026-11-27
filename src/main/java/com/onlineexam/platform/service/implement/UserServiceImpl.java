package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.dto.UserCreateRequest;
import com.onlineexam.platform.dto.UserResponse;
import com.onlineexam.platform.dto.UserUpdateRequest;
import com.onlineexam.platform.entity.Classroom;
import com.onlineexam.platform.entity.User;
import com.onlineexam.platform.entity.UserRole;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.ClassroomRepository;
import com.onlineexam.platform.repository.UserRepository;
import com.onlineexam.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserRole role, Pageable pageable) {
        Page<User> users = (role != null)
                ? userRepository.findByRole(role, pageable)
                : userRepository.findAll(pageable);

        return users.map(user -> new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getClassroom() != null ? user.getClassroom().getClassName() : null,
                user.isActive()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public UserUpdateRequest getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFullName(user.getFullName());
        userUpdateRequest.setEmail(user.getEmail());
        userUpdateRequest.setRole(user.getRole());
        userUpdateRequest.setClassroomId(user.getClassroom() != null ? user.getClassroom().getClassroomId() : null);
        userUpdateRequest.setActive(user.isActive());
        return userUpdateRequest;
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest userCreateRequest) {
        String username = userCreateRequest.getUsername().strip();
        String email = userCreateRequest.getEmail().strip().toLowerCase();
        if (userRepository.existsByUsernameIgnoreCase(username)){
            throw new BusinessRuleException("Username is already exists: " + username, "Tài khoản đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(email)){
            throw new BusinessRuleException("Email is already exists: " + email, "Email đã tồn tại");
        }
        User user = new User();
        user.setUsername(username);
        user.setFullName(userCreateRequest.getFullName().strip());
        user.setEmail(email);
        user.setRole(userCreateRequest.getRole());
        Classroom classroom = resolveClassroomByRole(userCreateRequest.getRole(), userCreateRequest.getClassroomId());
        user.setClassroom(classroom);
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        if (userRepository.existsByEmailIgnoreCaseAndUserIdNot(userUpdateRequest.getEmail().strip().toLowerCase(), userId)) {
            throw new BusinessRuleException("Email already exists", "Email đã tồn tại");
        }
        if (user.getUsername().equalsIgnoreCase(currentUsername) && !userUpdateRequest.isActive()) {
            throw new BusinessRuleException("Cannot deactivate self", "Không thể tự vô hiệu hóa tài khoản của chính mình");
        }
        boolean userIsAdminAndIsActive = user.getRole() == UserRole.ADMIN && user.isActive();
        boolean userUpdateIsAdminAndIsActive = userUpdateRequest.getRole() == UserRole.ADMIN && userUpdateRequest.isActive();
        if (userIsAdminAndIsActive && !userUpdateIsAdminAndIsActive && userRepository.countByRoleAndIsActiveIsTrue(UserRole.ADMIN) <= 1) {
            throw new BusinessRuleException("Cannot deactivate admin", "Không thể vô hiệu hóa hoặc đổi vai trò của tài khoản Admin hoạt động cuối cùng");
        }

        String newPassword = userUpdateRequest.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 8) {
                throw new BusinessRuleException("Password too short", "Mật khẩu mới phải có tối thiểu 8 ký tự");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        user.setFullName(userUpdateRequest.getFullName().strip());
        user.setEmail(userUpdateRequest.getEmail().strip().toLowerCase());
        Classroom classroom = resolveClassroomByRole(userUpdateRequest.getRole(), userUpdateRequest.getClassroomId());
        user.setClassroom(classroom);
        user.setRole(userUpdateRequest.getRole());
        user.setActive(userUpdateRequest.isActive());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (user.isActive()) {
            if (user.getUsername().equalsIgnoreCase(currentUsername)) {
                throw new BusinessRuleException("Cannot deactivate self", "Không thể tự vô hiệu hóa tài khoản của chính mình");
            }
            if (user.getRole() == UserRole.ADMIN && userRepository.countByRoleAndIsActiveIsTrue(UserRole.ADMIN) <= 1) {
                throw new BusinessRuleException("Cannot deactivate last active admin", "Không thể vô hiệu hóa tài khoản Admin hoạt động cuối cùng");
            }
        }

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    private Classroom resolveClassroomByRole(UserRole role, Long classroomId) {
        if (role == UserRole.STUDENT) {
            if (classroomId == null) {
                throw new BusinessRuleException("Student must have a classroom", "Tài khoản sinh viên bắt buộc phải chọn lớp học");
            }
            return classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom", "classroomId", classroomId));
        } else {
            if (classroomId != null) {
                throw new BusinessRuleException("Non-student cannot have a classroom", "Tài khoản Admin hoặc Giảng viên không được gán lớp học");
            }
            return null;
        }
    }
}
