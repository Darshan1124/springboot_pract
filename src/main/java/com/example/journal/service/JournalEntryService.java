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
    public List<JournalEntryDTO> getMyEntries(String username) {
        return journalEntryRepository.findByUser_Username(username)
                .stream()
                .map(journalEntryMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JournalEntryDTO> getAllEntries() {
        return journalEntryRepository.findAll()
                .stream()
                .map(journalEntryMapper::toDTO)
                .toList();
    }

    @Transactional
    public JournalEntryDTO createForCurrentUser(JournalEntryDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        JournalEntry entry = journalEntryMapper.toEntity(dto);
        entry.setUser(user);
        user.getJournalEntries().add(entry);

        return journalEntryMapper.toDTO(journalEntryRepository.save(entry));
    }

    @Transactional
    public JournalEntryDTO updateOwnEntry(Long id, JournalEntryDTO dto, String username) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));

        assertOwner(entry, username);

        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            entry.setTitle(dto.getTitle().trim());
        }
        if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
            entry.setContent(dto.getContent().trim());
        }

        return journalEntryMapper.toDTO(entry);
    }

    @Transactional
    public void deleteOwnEntry(Long id, String username) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));

        assertOwner(entry, username);

        User owner = entry.getUser();
        owner.getJournalEntries().remove(entry);
        journalEntryRepository.delete(entry);
    }

    private void assertOwner(JournalEntry entry, String username) {
        if (entry.getUser() == null || !entry.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own journal entries");
        }
    }
}