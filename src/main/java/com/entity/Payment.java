package com.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;

    @NotBlank(message = "Получатель обязателен")
    private String recipient;

    @NotBlank(message = "ИНН обязателен")
    @Pattern(
            regexp = "\\d{10}|\\d{12}",
            message = "ИНН должен содержать 10 или 12 цифр"
    )
    private String inn;

    @NotBlank(message = "Назначение обязательно")
    private String purpose;


    private String status;
    private String description;

    @NotBlank(message = "Создатель платежа обязателен")
    private String createdBy;

    private LocalDate updatedAt;
    private LocalDate createdAt;
    private LocalDate executionDate;

    @NotNull(message = "Валюта обязательна")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotNull(message = "Тип платежа обязателен")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @NotNull(message = "Приоритет обязателен")
    @Enumerated(EnumType.STRING)
    private PaymentPriority priority;

    public Payment() {
        this.createdAt = LocalDate.now();
        this.status = "created";
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentPriority getPriority() {
        return priority;
    }

    public void setPriority(PaymentPriority priority) {
        this.priority = priority;
    }
}
