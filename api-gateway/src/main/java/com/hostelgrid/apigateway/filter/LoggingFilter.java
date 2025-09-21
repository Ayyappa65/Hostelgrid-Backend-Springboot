package com.hostelgrid.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * A global filter that logs incoming requests and outgoing responses.
 * Logs the HTTP method, request path, remote address, and response status code.
 * Measures and logs the time taken to process each request.
 * Executes before other filters.
 * Uses SLF4J for logging.
 * Implements Ordered to set filter precedence.
 * Annotated with @Component to be detected by Spring.
 * 
 * The main difference between LoggingFilter and jwtAuthFilter is that
 * LoggingFilter is a global filter that logs all requests and responses,
 * while jwtAuthFilter is a specific filter that handles JWT authentication.
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        @SuppressWarnings("null")
		String remoteAddress = exchange.getRequest().getRemoteAddress() != null 
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
            : "unknown";
            
        long startTime = System.currentTimeMillis();
        log.info("Request: {} {} from {}", method, path, remoteAddress);

        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {

                @SuppressWarnings("null")
				int statusCode = exchange.getResponse().getStatusCode() != null 
                    ? exchange.getResponse().getStatusCode().value() 
                    : 0;
                long duration = System.currentTimeMillis() - startTime;
                log.info("Response: {} {} - Status: {} - Duration: {} ms", method, path, statusCode, duration);
            });
    }

    // Set the order to ensure this filter runs before others
    @Override
    public int getOrder() {
        return -1; // Execute before other filters
    }
}