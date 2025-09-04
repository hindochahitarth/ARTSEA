package com.art.artsea.service;

import com.art.artsea.repository.ArtworkRepository;
import com.art.artsea.repository.AuctionRepository;
import com.art.artsea.repository.CategoryRepository;
import com.art.artsea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long getTotalBuyers() {
        return userRepository.countBuyers();
    }

    public long getTotalSellers() {
        return userRepository.countSellers();
    }

    public long getActiveUsers() {
        return (userRepository.countActiveUsers()-1);
    }

    public long getPendingUsers() {
        return userRepository.countPendingUsers();
    }

    public long getLiveAuctionCount() {
        return auctionRepository.countLiveAuctions();
    }

    public long getUpcomingAuctionCount() {
        return auctionRepository.countUpcomingAuctions();
    }

    public long getPastAuctionCount() {
        return auctionRepository.countPastAuctions();
    }



    public long getApprovedArtworkCount() {
        return artworkRepository.countApprovedArtworks();
    }

    public long getPendingArtworkCount() {
        return artworkRepository.countPendingArtworks();
    }

    public long getTotalCategories() {
        return categoryRepository.countCategories();
    }


    //For seller
    public long getAppliedAuctions(Long sellerId) {
        return artworkRepository.countAppliedAuctions(sellerId);
    }
    public long getLiveArtworks(Long sellerId) {
        return artworkRepository.countLiveArtworks(sellerId);
    }
    public long getSellerApprovedArtworks(Long sellerId) {
        return artworkRepository.countSellerApprovedArtworks(sellerId);
    }
    public long getSellerPendingArtworks(Long sellerId) {
        return artworkRepository.countSellerPendingArtworks(sellerId);
    }


}