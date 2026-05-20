package com.example.journal.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterJobService {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Scheduled(cron = "*/10 * * * * *")
    public void IncrementCounter(){
        int val=counter.incrementAndGet();
        System.out.println(val);
    }

    public int getCounter() {
        return counter.get();
    }
}
