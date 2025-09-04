package com.art.artsea.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "auction_name", nullable = false, length = 50)
    private String auctionName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "auction_apply_start_time", nullable = false)
    private LocalDateTime applyStartTime;

    @Column(name = "auction_apply_end_time", nullable = false)
    private LocalDateTime applyEndTime;

    @Column(name = "status", columnDefinition = "ENUM('UPCOMING','ONGOING','ENDED') DEFAULT 'UPCOMING'")
    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.UPCOMING;


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    // Constructors
    public Auction() {
    }

    public Auction(String auctionName, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status) {
        this.auctionName = auctionName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and Setters
    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public void setAuctionName(String auctionName) {
        this.auctionName = auctionName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getApplyEndTime() {
        return applyEndTime;
    }

    public void setApplyEndTime(LocalDateTime applyEndTime) {
        this.applyEndTime = applyEndTime;
    }

    public LocalDateTime getApplyStartTime() {
        return applyStartTime;
    }

    public void setApplyStartTime(LocalDateTime applyStartTime) {
        this.applyStartTime = applyStartTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    // Enum for status
    public enum AuctionStatus {
        UPCOMING,
        ONGOING,
        ENDED
    }
}