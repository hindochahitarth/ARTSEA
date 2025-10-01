package com.art.artsea.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "slider")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slider_id")
    private Long sliderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false, referencedColumnName = "auction_id",
            foreignKey = @ForeignKey(name = "fk_slider_auction"))
    private Auction auction;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Slider() {}

    public Slider(Auction auction, String imageUrl) {
        this.auction = auction;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getSliderId() {
        return sliderId;
    }

    public void setSliderId(Long sliderId) {
        this.sliderId = sliderId;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // No setter for createdAt → it will be auto-set by DB
}
