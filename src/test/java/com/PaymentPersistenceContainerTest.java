package com;

import com.entity.Currency;
import com.entity.Payment;
import com.repository.PaymentRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
public class PaymentPersistenceContainerTest {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine")
                    .withDatabaseName("payments_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void shouldSavePaymentInPostgresContainer() {

        // Arrange
        Payment payment = new Payment();

        payment.setAmount(new BigDecimal("7777.00"));
        payment.setCurrency(Currency.EUR);
        payment.setRecipient("Testcontainers Company");
        payment.setInn("1234567890");
        payment.setPurpose("Integration test");
        payment.setDescription("Testcontainers practice");

        // Act — сохраняем через настоящий JPA Repository
        Payment saved = paymentRepository.saveAndFlush(payment);

        // Assert — проверяем запись через SQL
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM payment WHERE id = ?",
                String.class,
                saved.getId()
        );

        BigDecimal amount = jdbcTemplate.queryForObject(
                "SELECT amount FROM payment WHERE id = ?",
                BigDecimal.class,
                saved.getId()
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(status).isEqualTo("created");
        assertThat(amount)
                .isEqualByComparingTo("7777.00");
    }
}
