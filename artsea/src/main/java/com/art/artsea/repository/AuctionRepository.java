package com.art.artsea.repository;

import com.art.artsea.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStartTimeAfter(LocalDateTime dateTime);

    List<Auction> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime start, LocalDateTime end);

    List<Auction> findByEndTimeBefore(LocalDateTime dateTime);

    List<Auction> findByStatus(Auction.AuctionStatus status);
}

