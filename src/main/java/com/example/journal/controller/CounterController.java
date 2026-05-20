package com.example.journal.controller;

import com.example.journal.service.CounterJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.css.Counter;

@RestController("/getcount")
public class CounterController {

    @Autowired
    private CounterJobService counterJobService;
    @GetMapping
    public int getCounter() {
        return counterJobService.getCounter();
    }
}
