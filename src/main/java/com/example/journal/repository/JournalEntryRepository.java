package com.example.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.journal.entity.JournalEntry;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    
}
