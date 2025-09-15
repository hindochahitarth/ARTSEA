package com.art.artsea.model;

public class BidMessage {
    private Long artworkId;
    private String bidderName;
    private String bidAmount; // formatted string for UI

    public BidMessage(Long artworkId, String bidderName, String bidAmount) {
        this.artworkId = artworkId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
    }

    // getters
    public Long getArtworkId() { return artworkId; }
    public String getBidderName() { return bidderName; }
    public String getBidAmount() { return bidAmount; }
}
