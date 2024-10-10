package com.auth.auth_service.model;

import com.auth.auth_service.shared.constant.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"enumName"})})
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleEnum enumName;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

}
