package com.auth.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.generator.values.GeneratedValues;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_token_session")
public class TokenSession {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false, unique = true, length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private boolean isRevoked = false;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
