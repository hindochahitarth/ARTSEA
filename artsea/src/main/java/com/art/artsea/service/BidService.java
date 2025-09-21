package com.art.artsea.service;

import com.art.artsea.model.Artwork;
import com.art.artsea.model.Bid;
import com.art.artsea.model.BidMessage;
import com.art.artsea.model.User;
import com.art.artsea.repository.ArtworkRepository;
import com.art.artsea.repository.BidRepository;
import com.art.artsea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    @Autowired
    public BidService(BidRepository bidRepository,
                      ArtworkRepository artworkRepository,
                      UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.artworkRepository = artworkRepository;
        this.userRepository = userRepository;
    }


    // Get highest bid for a specific artwork
    public Optional<Bid> getHighestBidForArtwork(Long artworkId) {
        return bidRepository.findTopByArtwork_ArtworkIdOrderByBidAmountDescBidTimeDesc(artworkId);
    }

    // Get highest bidder ID for a specific artwork
    public Optional<Long> getHighestBidderId(Long artworkId) {
        return bidRepository.findTopByArtwork_ArtworkIdOrderByBidAmountDescBidTimeDesc(artworkId)
                .map(bid -> bid.getUser().getUserId());
    }

    public Optional<String> getHighestBidderName(Long artworkId) {
        return bidRepository.findTopByArtwork_ArtworkIdOrderByBidAmountDescBidTimeDesc(artworkId)
                .map(bid -> bid.getUser().getUsername());
    }




    // Already present
    public Optional<Double> getCurrentBidForArtwork(Long artworkId) {
        return bidRepository.findHighestBidByArtworkId(artworkId);
    }

    // New: Save bid and return DTO for WebSocket
    public BidMessage saveBid(Long artworkId, Long userId, BigDecimal amount) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bid bid = new Bid(artwork, user, amount);
        bidRepository.save(bid);

        return new BidMessage(
                artworkId,
                user.getUsername(), // adjust to your User class field
                "₹" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
        );
    }

    public Optional<Bid> getBidById(Long bidId) {
        return bidRepository.findById(bidId);
    }

    //for profile bids for live auction artwork
    // Fetch all bids by user
    // Fetch all bids by user
    public List<Bid> getBidsByUser(Long userId) {
        return bidRepository.findByUserUserId(userId);
    }

    // Fetch highest bid for an artwork
    public Optional<Double> getHighestBidAmountForArtwork(Long artworkId) {
        return bidRepository.findMaxBidAmountByArtworkId(artworkId);
    }

}
