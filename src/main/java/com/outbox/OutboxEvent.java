package com.outbox;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean published = false;

    protected OutboxEvent() {
    }

    public OutboxEvent(Long paymentId, String status) {
        this.paymentId = paymentId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
