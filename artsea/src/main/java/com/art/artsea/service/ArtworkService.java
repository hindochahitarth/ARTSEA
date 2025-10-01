package com.art.artsea.service;

import com.art.artsea.model.Artwork;
import com.art.artsea.model.Auction;
import com.art.artsea.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    public Artwork saveArtwork(Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    public List<Artwork> getAllAuctions() {
        return artworkRepository.findAll(Sort.by(Sort.Direction.DESC, "artworkId"));
    }
    public Artwork getArtworkById(Long id) {
        return artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
    }

    public void rejectArtwork(Long artworkId, String rejectionReason) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        artwork.setStatus(Artwork.ArtworkStatus.REJECTED); // assuming enum
        artwork.setRejectionReason(rejectionReason);

        artworkRepository.save(artwork);
    }

    public List<Artwork> getArtworkByUserId(Long userId) {
        return artworkRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Fetch all approved artworks for a specific auction
    public List<Artwork> getApprovedArtworksByAuctionId(Long auctionId) {
        return artworkRepository.findApprovedArtworksByAuctionId(auctionId);
    }



}
