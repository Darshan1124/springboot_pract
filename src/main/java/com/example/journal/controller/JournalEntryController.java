package com.example.journal.controller;

import com.example.journal.dto.JournalEntryDTO;
import com.example.journal.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getAll() {
        return ResponseEntity.ok(journalEntryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(journalEntryService.findById(id));
    }

    @PostMapping("/{username}")
    public ResponseEntity<JournalEntryDTO> createEntry(@PathVariable String username,
                                                       @RequestBody JournalEntryDTO dto) {
        JournalEntryDTO savedEntry = journalEntryService.saveEntry(dto, username);
        return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> updateJournalById(@PathVariable Long id,
                                                             @RequestBody JournalEntryDTO newEntry) {
        return ResponseEntity.ok(journalEntryService.updateEntry(id, newEntry));
    }

    @DeleteMapping("/{id}/{username}")
    public ResponseEntity<Void> deleteJournalEntryById(@PathVariable Long id,
                                                       @PathVariable String username) {
        journalEntryService.deleteEntry(id, username);
        return ResponseEntity.noContent().build();
    }
}