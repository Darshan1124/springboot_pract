package com.example.journal.controller;

import com.example.journal.dto.ExternalApiResponse;
import com.example.journal.dto.ExternalCreateRequest;
import com.example.journal.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/external")
public class ExternalRequestController {

    @Autowired
    ExternalApiService externalApiService;

    @GetMapping
    public String externalReqGet(){
        return externalApiService.externalReqGet();
    }

    @PostMapping
    public ExternalApiResponse externalReqPost(@RequestBody ExternalCreateRequest body){
        return externalApiService.externalReqPost(body);
    }
}
