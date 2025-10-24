package com.hostelgrid.studentservice.client;

import com.hostelgrid.studentservice.filter.JwtContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class HostelGraphQLClient {

    private final WebClient webClient;
    private final String hostelGraphQLUrl;

    public HostelGraphQLClient(@Value("${external.services.hostel-service.graphql-url}") String hostelGraphQLUrl) {
        this.hostelGraphQLUrl = hostelGraphQLUrl;
        this.webClient = WebClient.builder().build();
    }

    public Mono<Map<String, Object>> getHostels() {
        String authToken = JwtContextFilter.getCurrentJwtToken();
        return getHostels(authToken);
    }

    public Mono<Map<String, Object>> getHostels(String authToken) {
        String query = """
            {
                hostels {
                    id
                    name
                    email
                    contactNumber
                    status
                }
            }
            """;

        Map<String, Object> requestBody = Map.of("query", query);

        return webClient.post()
                .uri(hostelGraphQLUrl)
                .header("Authorization", authToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public Mono<Map<String, Object>> getHostelById(Long id) {
        String authToken = JwtContextFilter.getCurrentJwtToken();
        return getHostelById(id, authToken);
    }

    public Mono<Map<String, Object>> getHostelById(Long id, String authToken) {
        String query = """
            query GetHostel($id: ID!) {
                hostel(id: $id) {
                    id
                    name
                    email
                    contactNumber
                    status
                }
            }
            """;

        Map<String, Object> variables = Map.of("id", id);
        Map<String, Object> requestBody = Map.of("query", query, "variables", variables);

        return webClient.post()
                .uri(hostelGraphQLUrl)
                .header("Authorization", authToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    }
}