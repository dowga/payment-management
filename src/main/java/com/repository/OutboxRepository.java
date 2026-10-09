package com.repository;

import com.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop20ByPublishedFalseOrderByIdAsc();
}