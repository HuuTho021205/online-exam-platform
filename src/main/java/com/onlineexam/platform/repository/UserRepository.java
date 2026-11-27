package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.User;
import com.onlineexam.platform.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    int countByClassroom_ClassroomId(Long classroomClassroomId);
    List<User> findByRole(UserRole role);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndUserIdNot(String email, Long userId);
    int countByRoleAndIsActiveIsTrue(UserRole role);
    boolean existsByUsernameIgnoreCase(String username);
}
