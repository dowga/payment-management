package com.kafka;

import com.entity.Payment;
import com.event.PaymentExecutedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    private final KafkaTemplate<String, PaymentExecutedEvent> kafkaTemplate;

    public PaymentEventPublisher(
            KafkaTemplate<String, PaymentExecutedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishExecuted(Payment payment) {

        PaymentExecutedEvent event = new PaymentExecutedEvent(
                payment.getId(),
                payment.getStatus()
        );

        kafkaTemplate.send(
                "payment-events",
                payment.getId().toString(),
                event
        ).join();
    }
}
