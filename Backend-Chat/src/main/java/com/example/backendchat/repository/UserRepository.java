package com.example.backendchat.repository;

import com.example.backendchat.constant.ErrorMessage;
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
public interface UserRepository extends JpaRepository<User, String> {

  @Query("SELECT u FROM User u WHERE u.id = ?1")
  Optional<User> findById(String id);

  @Query("SELECT u FROM User u WHERE u.username = ?1")
  Optional<User> findByUsername(String username);
  @Query("SELECT u FROM User u WHERE u.email = ?1")
  Optional<User> findUserByEmail(String email);
  default User getUser(UserPrincipal currentUser) {
    return findByUsername(currentUser.getUsername())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME,
            new String[]{currentUser.getUsername()}));
  }

  boolean existsByEmail(String email);
  boolean existsByUsername(String username);
  @Query(value = "SELECT users.* FROM users\n" +
          "JOIN (\n" +
          "    SELECT user_id, MAX(created_date) AS max_created_date\n" +
          "    FROM (\n" +
          "        SELECT receiver_id AS user_id, created_date\n" +
          "        FROM messages\n" +
          "        WHERE sender_id = ?1\n" +
          "        UNION\n" +
          "        SELECT sender_id AS user_id, created_date\n" +
          "        FROM messages\n" +
          "        WHERE receiver_id = ?1\n" +
          "    ) AS temp\n" +
          "    GROUP BY user_id\n" +
          ") AS sorted_users\n" +
          "ON users.id = sorted_users.user_id\n" +
          "ORDER BY sorted_users.max_created_date DESC", nativeQuery = true)
  Page<User> getAllUserConversation(String userId, Pageable pageable);

}
