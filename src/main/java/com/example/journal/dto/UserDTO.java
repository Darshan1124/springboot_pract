package com.example.journal.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    // We send back a list of DTOs, not the database entities!
    private List<JournalEntryDTO> journalEntries; 
}
