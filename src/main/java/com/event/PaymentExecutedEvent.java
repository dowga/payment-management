package com.event;

public record PaymentExecutedEvent(
        Long paymentId,
        String status
) {
}
