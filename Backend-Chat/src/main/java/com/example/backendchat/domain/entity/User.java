package com.example.backendchat.domain.entity;

import com.example.backendchat.domain.entity.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User extends DateAuditing {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String id;
    @Nationalized
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private String avatar;
    private String status;
    private LocalDateTime activityTime;
    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_USER_ROLE"))
    private Role role;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Code code;
}