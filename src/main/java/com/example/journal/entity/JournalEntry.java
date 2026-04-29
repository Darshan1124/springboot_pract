package com.example.journal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
@Data // Lombok: Generates getters, setters, toString, equals, hashcode
@NoArgsConstructor // Lombok: Required by JPA to instantiate entities
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Replaces MongoDB's ObjectId

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp // Automatically assigns the timestamp on insert. Replaces manual setter.
    @Column(updatable = false)
    private LocalDateTime date;

    // Relational Mapping: Links the entry back to a specific user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates the Foreign Key column in the DB
    private User user;
}
