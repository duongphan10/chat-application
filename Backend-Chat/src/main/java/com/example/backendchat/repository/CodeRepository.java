package com.example.backendchat.repository;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.domain.entity.Code;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {
    @Query(value = "SELECT * FROM codes WHERE user_id = ?1", nativeQuery = true)
    Code findByUserId(String userId);
}
