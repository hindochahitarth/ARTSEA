package com.art.artsea.service;

import com.art.artsea.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BidService {

    private final BidRepository bidRepository;

    @Autowired
    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public Optional<Double> getCurrentBidForArtwork(Long artworkId) {
        return bidRepository.findHighestBidByArtworkId(artworkId);
    }
}
