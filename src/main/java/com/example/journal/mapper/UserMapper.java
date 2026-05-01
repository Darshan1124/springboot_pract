package com.example.journal.mapper;

import com.example.journal.dto.UserDTO;
import com.example.journal.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final JournalEntryMapper journalEntryMapper;

    public UserDTO toDTO(User user) {
        return toDTO(user, true);
    }

    public UserDTO toDTO(User user, boolean includeJournalEntries) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        if (includeJournalEntries && user.getJournalEntries() != null) {
            dto.setJournalEntries(
                    user.getJournalEntries()
                            .stream()
                            .map(journalEntryMapper::toDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        return user;
    }
}