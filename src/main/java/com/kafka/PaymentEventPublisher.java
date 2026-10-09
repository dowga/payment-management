package com.kafka;

import com.event.PaymentExecutedEvent;
import com.outbox.OutboxEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    private final KafkaTemplate<String, PaymentExecutedEvent> kafkaTemplate;

    public PaymentEventPublisher(
            KafkaTemplate<String, PaymentExecutedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishExecuted(OutboxEvent outboxEvent) {

        PaymentExecutedEvent event = new PaymentExecutedEvent(
                outboxEvent.getPaymentId(),
                outboxEvent.getStatus()
        );

        kafkaTemplate.send(
                "payment-events",
                outboxEvent.getPaymentId().toString(),
                event
        ).join();
    }
}