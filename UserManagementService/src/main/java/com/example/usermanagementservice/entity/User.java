package com.example.usermanagementservice.entity;

import com.example.usermanagementservice.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID userId;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private Role role;

}
