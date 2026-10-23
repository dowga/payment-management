package com.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import java.time.Duration;

@RestController
@RequestMapping("/api/mock-bank")
@ConditionalOnProperty(
        name = "demo.bank.enabled",
        havingValue = "true"
)
public class BankMockDemoController {

    private static final Logger log =
            LoggerFactory.getLogger(BankMockDemoController.class);

    private final RestClient bankClient;

    public BankMockDemoController(
            RestClient.Builder builder,
            @Value("${bank.api.base-url}") String bankUrl
    ) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(1));
        factory.setReadTimeout(Duration.ofSeconds(1));

        this.bankClient = builder
                .baseUrl(bankUrl)
                .requestFactory(factory)
                .build();
    }

    @GetMapping("/check/{paymentId}")
    public ResponseEntity<String> checkPayment(
            @PathVariable Long paymentId
    ) {
        try {
            String response = bankClient.get()
                    .uri("/bank/check/{id}", paymentId)
                    .retrieve()
                    .body(String.class);

            log.info("Bank approved paymentId={}", paymentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (HttpServerErrorException ex) {

            log.error(
                    "Bank API failed for paymentId={}, status={}",
                    paymentId,
                    ex.getStatusCode(),
                    ex
            );

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\":\"BANK_UNAVAILABLE\"}");

        } catch (ResourceAccessException ex) {

            log.error(
                    "Bank API connection/timeout error for paymentId={}",
                    paymentId,
                    ex
            );

            return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"BANK_TIMEOUT\"}");
        }
    }
}
