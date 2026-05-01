package com.example.journal.service;

import com.example.journal.dto.JournalEntryDTO;
import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.mapper.JournalEntryMapper;
import com.example.journal.repository.JournalEntryRepository;
import com.example.journal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;
    private final JournalEntryMapper journalEntryMapper;

    @Transactional(readOnly = true)
    public List<JournalEntryDTO> getAll() {
        return journalEntryRepository.findAll()
                .stream()
                .map(journalEntryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JournalEntryDTO findById(Long id) {
        return journalEntryRepository.findById(id)
                .map(journalEntryMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));
    }

    @Transactional
    public JournalEntryDTO saveEntry(JournalEntryDTO dto, String username) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().trim().isEmpty()
                || dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and content are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        JournalEntry entry = journalEntryMapper.toEntity(dto);
        entry.setUser(user);

        user.getJournalEntries().add(entry);

        JournalEntry saved = journalEntryRepository.save(entry);
        return journalEntryMapper.toDTO(saved);
    }

    @Transactional
    public JournalEntryDTO updateEntry(Long id, JournalEntryDTO newEntry) {
        JournalEntry oldEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));

        if (newEntry.getTitle() != null && !newEntry.getTitle().trim().isEmpty()) {
            oldEntry.setTitle(newEntry.getTitle().trim());
        }

        if (newEntry.getContent() != null && !newEntry.getContent().trim().isEmpty()) {
            oldEntry.setContent(newEntry.getContent().trim());
        }

        return journalEntryMapper.toDTO(oldEntry);
    }

    @Transactional
    public void deleteEntry(Long entryId, String username) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));

        if (entry.getUser() == null || !entry.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This journal entry does not belong to the user");
        }

        User user = entry.getUser();
        user.getJournalEntries().remove(entry);

        journalEntryRepository.delete(entry);
    }
}