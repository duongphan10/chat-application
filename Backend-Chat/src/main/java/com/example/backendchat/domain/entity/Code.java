package com.example.backendchat.domain.entity;

import com.example.backendchat.domain.entity.common.DateAuditing;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "codes")
public class Code extends DateAuditing {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String id;
    @Column(nullable = false)
    private String verificationCode;
    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true)
    private User user;
}
