package com.art.artsea.service;

import com.art.artsea.exception.ResourceNotFoundException;
import com.art.artsea.model.Auction;
import com.art.artsea.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public Auction saveAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public List<Auction> getUpcomingAuctions() {
        return auctionRepository.findByStartTimeAfter(LocalDateTime.now());
    }
    public List<Auction> getPastAuctions() {
        return auctionRepository.findByEndTimeBefore(LocalDateTime.now());
    }

    public List<Auction> getLiveAuctions() {
        return auctionRepository.findLiveAuctions();
    }

    public List<Auction> getOngoingAuctions() {
        LocalDateTime now = LocalDateTime.now();
        return auctionRepository.findByStartTimeBeforeAndEndTimeAfter(now, now);
    }

    public List<Auction> getCompletedAuctions() {
        LocalDateTime now = LocalDateTime.now();
        return auctionRepository.findByEndTimeBefore(now);
    }

    public Auction getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));
    }

    public Optional<Auction> findAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }

    public boolean isAuctionAcceptingApplications(Auction auction) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(auction.getApplyStartTime()) &&
                now.isBefore(auction.getApplyEndTime());
    }

    @Transactional
    public void deleteAuction(Long auctionId) {
        try {
            auctionRepository.deleteById(auctionId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Auction not found with id: " + auctionId);
        }
    }


    @Transactional
    public Auction updateAuction(Long auctionId, Auction auctionDetails) {
        Auction auction = getAuctionById(auctionId);

        auction.setAuctionName(auctionDetails.getAuctionName());
        auction.setStartTime(auctionDetails.getStartTime());
        auction.setEndTime(auctionDetails.getEndTime());
        auction.setApplyStartTime(auctionDetails.getApplyStartTime());
        auction.setApplyEndTime(auctionDetails.getApplyEndTime());
        auction.setStatus(auctionDetails.getStatus());

        return auctionRepository.save(auction);
    }

    public boolean existsById(Long auctionId) {
        return auctionRepository.existsById(auctionId);
    }
}