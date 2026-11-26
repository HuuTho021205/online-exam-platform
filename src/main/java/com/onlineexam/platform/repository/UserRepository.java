package com.onlineexam.platform.repository;

import com.onlineexam.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    int countByClassroom_ClassroomId(Long classroomClassroomId);
}
