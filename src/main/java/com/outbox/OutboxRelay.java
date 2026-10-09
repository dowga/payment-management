package com.outbox;

import com.kafka.PaymentEventPublisher;
import com.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxRelay {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxRepository outboxRepository;
    private final PaymentEventPublisher publisher;

    public OutboxRelay(
            OutboxRepository outboxRepository,
            PaymentEventPublisher publisher) {

        this.outboxRepository = outboxRepository;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {

        for (OutboxEvent event :
                outboxRepository.findTop20ByPublishedFalseOrderByIdAsc()) {

            try {
                publisher.publishExecuted(event);

                event.setPublished(true);
                outboxRepository.save(event);

            } catch (Exception exception) {

                log.warn(
                        "Не удалось опубликовать Outbox event id={}",
                        event.getId(),
                        exception
                );

                break;
            }
        }
    }
}
