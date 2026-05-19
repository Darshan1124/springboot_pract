package com.example.journal.service;

import com.example.journal.dto.ExternalApiResponse;
import com.example.journal.dto.ExternalCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiService {

    
    private final RestClient restClient;
    public ExternalApiService(
            @Value("${external.api.base-url}") String baseUrl,
            @Value("${external.api.key}") String apiKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    public String externalReqGet(){
        return restClient.get().uri("/hello-world-get").retrieve().body(String.class);
    }
    public ExternalApiResponse externalReqPost(ExternalCreateRequest body){
        return restClient.post().uri("/hello-world-post").body(body).retrieve().body(ExternalApiResponse.class);
    }
}
