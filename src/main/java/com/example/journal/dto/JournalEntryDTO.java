package com.example.journal.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JournalEntryDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String username; // Just the username, not the whole User object!
}
