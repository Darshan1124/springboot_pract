package com.example.journal.controller;

import com.example.journal.dto.JournalEntryDTO;
import com.example.journal.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<JournalEntryDTO>> getMyEntries(Authentication authentication) {
        return ResponseEntity.ok(journalEntryService.getMyEntries(authentication.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JournalEntryDTO>> getAllEntries() {
        return ResponseEntity.ok(journalEntryService.getAllEntries());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<JournalEntryDTO> createEntry(Authentication authentication,
                                                       @RequestBody JournalEntryDTO dto) {
        JournalEntryDTO saved = journalEntryService.createForCurrentUser(dto, authentication.getName());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<JournalEntryDTO> updateEntry(Authentication authentication,
                                                       @PathVariable Long id,
                                                       @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.updateOwnEntry(id, dto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteEntry(Authentication authentication, @PathVariable Long id) {
        journalEntryService.deleteOwnEntry(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}