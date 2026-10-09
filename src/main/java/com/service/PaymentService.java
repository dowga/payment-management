package com.service;

import com.entity.Payment;
import com.entity.Currency;
import org.springframework.data.jpa.domain.Specification;
import com.repository.PaymentRepository;
import com.service.exception.PaymentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.outbox.OutboxEvent;
import com.repository.OutboxRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    public Payment createPayment(Payment payment) {
        LocalDate now = LocalDate.now();

        payment.setCreatedAt(now);
        payment.setStatus("created");
        payment.setExecutionDate(null);

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment updatePayment(Long id, Payment updatedPayment) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setAmount(updatedPayment.getAmount());
        payment.setRecipient(updatedPayment.getRecipient());
        payment.setInn(updatedPayment.getInn());
        payment.setPurpose(updatedPayment.getPurpose());
        payment.setDescription(updatedPayment.getDescription());
        payment.setCurrency(updatedPayment.getCurrency());

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment executePayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if ("executed".equals(payment.getStatus())) {
            throw new IllegalStateException("Платёж уже выполнен");
        }

        if (!"created".equals(payment.getStatus())) {
            throw new IllegalStateException(
                    "Платёж нельзя выполнить в текущем статусе: "
                            + payment.getStatus()
            );
        }

        payment.setStatus("executed");
        payment.setExecutionDate(LocalDate.now());

        Payment savedPayment = paymentRepository.save(payment);

        outboxRepository.save(
                new OutboxEvent(
                        savedPayment.getId(),
                        savedPayment.getStatus()
                )
        );

        return savedPayment;
    }

    public Page<Payment> searchPayments(
            String status,
            Currency currency,
            String recipient,
            Pageable pageable) {

        Specification<Payment> spec = Specification.where(null);

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (currency != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("currency"), currency));
        }

        if (recipient != null && !recipient.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("recipient")),
                            "%" + recipient.toLowerCase() + "%"
                    ));
        }

        return paymentRepository.findAll(spec, pageable);
    }
}
