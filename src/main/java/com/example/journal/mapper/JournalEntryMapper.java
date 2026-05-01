package com.example.journal.mapper;

import com.example.journal.dto.JournalEntryDTO;
import com.example.journal.entity.JournalEntry;
import org.springframework.stereotype.Component;

@Component
public class JournalEntryMapper {

    public JournalEntryDTO toDTO(JournalEntry entry) {
        if (entry == null) {
            return null;
        }

        JournalEntryDTO dto = new JournalEntryDTO();
        dto.setId(entry.getId());
        dto.setTitle(entry.getTitle());
        dto.setContent(entry.getContent());
        dto.setDate(entry.getDate());

        if (entry.getUser() != null) {
            dto.setUsername(entry.getUser().getUsername());
        }

        return dto;
    }

    public JournalEntry toEntity(JournalEntryDTO dto) {
        if (dto == null) {
            return null;
        }

        JournalEntry entry = new JournalEntry();
        entry.setId(dto.getId());
        entry.setTitle(dto.getTitle());
        entry.setContent(dto.getContent());
        return entry;
    }
}