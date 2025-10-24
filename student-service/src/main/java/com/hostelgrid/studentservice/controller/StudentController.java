package com.hostelgrid.studentservice.controller;

import com.hostelgrid.studentservice.client.HostelGraphQLClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @Autowired
    private HostelGraphQLClient hostelGraphQLClient;

    @GetMapping("/hostels")
    public Mono<ResponseEntity<Map<String, Object>>> getHostels() {
        return hostelGraphQLClient.getHostels()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/hostels/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getHostelById(@PathVariable Long id) {
        return hostelGraphQLClient.getHostelById(id)
                .map(ResponseEntity::ok);
    }
}