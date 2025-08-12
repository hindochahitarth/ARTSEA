package com.art.artsea.model;

import com.art.artsea.model.Auction;
import com.art.artsea.model.Category;
import com.art.artsea.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "artworks")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artwork_id")
    private Long artworkId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    @Column(name = "artist_name", nullable = false, length = 100)
    private String artistName;

    @Column(name = "starting_price", nullable = false)
    private Double startingPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Status of the artwork (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArtworkStatus status = ArtworkStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // Helper methods
    public boolean isApproved() {
        return status == ArtworkStatus.APPROVED;
    }

    public boolean isPending() {
        return status == ArtworkStatus.PENDING;
    }



    public enum ArtworkStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    // Builder pattern for easy creation
    public static ArtworkBuilder builder() {
        return new ArtworkBuilder();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Long getArtworkId() {
        return artworkId;
    }

    public String getArtistName() {
        return artistName;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ArtworkStatus getStatus() {
        return status;
    }

    public void setArtworkId(Long artworkId) {
        this.artworkId = artworkId;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(ArtworkStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getTitle() {
        return title;
    }

//    public void setTitle(String title) {
//        this.title = title;
//    }

    public String getDescription() {
        return description;
    }

//    public void setDescription(String description) {
//        this.description = description;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static class ArtworkBuilder {
        private final Artwork artwork = new Artwork();

        public ArtworkBuilder title(String title) {
            artwork.setTitle(title);
            return this;
        }

        public ArtworkBuilder description(String description) {
            artwork.setDescription(description);
            return this;
        }

        public ArtworkBuilder imageUrl(String imageUrl) {
            artwork.setImageUrl(imageUrl);
            return this;
        }

        public ArtworkBuilder artistName(String artistName) {
            artwork.setArtistName(artistName);
            return this;
        }

        public ArtworkBuilder startingPrice(Double startingPrice) {
            artwork.setStartingPrice(startingPrice);
            return this;
        }

        public ArtworkBuilder user(User user) {
            artwork.setUser(user);
            return this;
        }

        public ArtworkBuilder auction(Auction auction) {
            artwork.setAuction(auction);
            return this;
        }

        public ArtworkBuilder category(Category category) {
            artwork.setCategory(category);
            return this;
        }

        public Artwork build() {
            return artwork;
        }
    }
}