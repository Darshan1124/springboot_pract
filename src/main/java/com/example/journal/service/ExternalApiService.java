package com.example.journal.service;

import com.example.journal.dto.ExternalApiResponse;
import com.example.journal.dto.ExternalCreateRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiService {

    @Value("S{external.api.base-url}")
    private String baseUrl;
    @Value("S{external.api.API_KEY}")
    private String api_key;

    private RestClient restClient;
//    public ExternalApiService() {
//        this.restClient = RestClient.builder().baseUrl(baseUrl).defaultHeader("x-api-key",api_key).build();
//    }


//    @PostConstruct is a method annotation that tells Spring:
//
//            “After this bean is created and all its dependencies are injected, run this method once.”
//
//    It runs one time per bean, during startup, before the bean is used by the rest of the app.
//
//    How it works
//
//    Spring creates the object first.
//
//    Then it injects dependencies like:
//
//    @Value
//    @Autowired
//    constructor arguments
//
//    After that, Spring calls the method marked with @PostConstruct.
//
//    So the order is:
//
//    object is constructed
//    fields/dependencies are injected
//    @PostConstruct method runs
//    bean is ready for use
//
//    In Spring Boot 4 / Spring Framework 6+, use:
//
//            import jakarta.annotation.PostConstruct;
//    What it is good for
//
//    Use it when you need setup that depends on injected values, for example:
//
//    building a client after @Value properties are loaded
//    checking config at startup
//    printing startup info
//    initializing caches or precomputed data

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", api_key)
                .build();

        System.out.println("ExternalApiService initialized with baseUrl = " + baseUrl);
    }

    public String externalReqGet(){
        return restClient.get().uri("/hello-world-get").retrieve().body(String.class);
    }
    public ExternalApiResponse externalReqPost(ExternalCreateRequest body){
        return restClient.post().uri("/hello-world-post").body(body).retrieve().body(ExternalApiResponse.class);
    }
}
