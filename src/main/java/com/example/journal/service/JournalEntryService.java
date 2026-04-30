package com.example.journal.service;

import com.example.journal.dto.JournalEntryDTO;
import com.example.journal.entity.JournalEntry;
import com.example.journal.entity.User;
import com.example.journal.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: Autowires 'final' dependencies automatically
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<JournalEntryDTO> getAll() {
    	List<JournalEntry> entries = journalEntryRepository.findAll();
    	List<JournalEntryDTO> cur = entries.stream()
                .map(entry -> convertToDTO(entry)) // <-- Changed this line
                .collect(Collectors.toList());
    	return cur;
    }

    @Transactional(readOnly = true)
    public Optional<JournalEntryDTO> findById(Long id) {
    	Optional<JournalEntry> optionalEntry= journalEntryRepository.findById(id);
    	return optionalEntry.map(entry -> convertToDTO(entry));
    }

    @Transactional
    public void deleteById(Long id, String username) {
        // 1. Find the user making the request
        User user = userService.findByUsername(username).orElseThrow();

        // 2. Remove the specific entry from this user's list
        // Because orphanRemoval = true, Hibernate will automatically delete it from MySQL!
        boolean removed = user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));
        
        // (Optional) You can add logic here to throw an exception if 'removed' is false, 
        // meaning the entry didn't belong to them or didn't exist.
    }

    @Transactional // Unlocks Hibernate Dirty Checking
    public JournalEntry updateEntry(Long id, JournalEntry newEntry) {
        JournalEntry oldEntry = journalEntryRepository.findById(id).orElse(null);
        
        if (oldEntry != null) {
            // Null and empty checks to prevent overwriting valid data
            if (newEntry.getTitle() != null && !newEntry.getTitle().trim().isEmpty()) {
                oldEntry.setTitle(newEntry.getTitle());
            }
            if (newEntry.getContent() != null && !newEntry.getContent().trim().isEmpty()) {
                oldEntry.setContent(newEntry.getContent());
            }
            
            // Notice: No explicit journalEntryRepository.save(oldEntry) is called here.
            // Because the method is @Transactional, modifying the 'oldEntry' automatically
            // triggers a database UPDATE when the transaction commits.
            return oldEntry;
        }
        return null;
    }
    
 // Inside JournalEntryService.java

    @Transactional
    public JournalEntry saveEntry(JournalEntry journalEntry, String username) {
        // 1. Find the user
        User user = userService.findByUsername(username).orElseThrow();
        
        // 2. Set the relationship on both sides
        journalEntry.setUser(user);
        user.getJournalEntries().add(journalEntry);
        
        // 3. Save the entry (The user will be updated automatically if cascaded, or save entry directly)
        return journalEntryRepository.save(journalEntry);
    }
    
 // Add this inside JournalEntryService.java

    public JournalEntryDTO convertToDTO(JournalEntry entry) {
        JournalEntryDTO dto = new JournalEntryDTO();
        dto.setId(entry.getId());
        dto.setTitle(entry.getTitle());
        dto.setContent(entry.getContent());
        dto.setDate(entry.getDate());
        
        // Check if the user exists to prevent NullPointerExceptions
        if (entry.getUser() != null) {
            dto.setUsername(entry.getUser().getUsername());
        }
        
        return dto;
    }
}

