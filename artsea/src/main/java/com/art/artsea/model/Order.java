package com.art.artsea.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    private Double amount;

    private String paymentId;

    private String status; // PENDING, SELLER_PROCESSING, SELLER_PROCESSED

    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    private String trackingId;

    private LocalDateTime orderDate = LocalDateTime.now();

    // getters and setters...

    public Long getOrderId() {
        return orderId;
    }

    public User getUser() {
        return user;
    }

    public Bid getBid() {
        return bid;
    }

    public Double getAmount() {
        return amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}

