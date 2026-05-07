package com.example.journal.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<JournalEntry> journalEntries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RefreshToken> refreshTokens = new ArrayList<>();
}


//{
//        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb3VybmFsLWFwcCIsInN1YiI6InJvb3QiLCJleHAiOjE3NzgwOTQwODAsImlhdCI6MTc3ODA5MzE4MCwidXNlcklkIjozLCJqdGkiOiIxOTAyZTIyNS1mNWMxLTQ0YWEtOGZhMy01NTEzNWI5NDQ1NjYifQ.toerQAHrQPpmdZGDZh0KUbgswDJW3M7QWlo5fZoBJ1g",
//        "refreshToken": "GDdLJhZoL2j-WS0S4VlC402RSqDt_65hqhbg167dHsOY_Kd4F8Ae1uxRvS7iKgSNElgy82Dr9o6EmkMU5-viYQ",
//        "tokenType": "Bearer",
//        "expiresInSeconds": 900
//        }