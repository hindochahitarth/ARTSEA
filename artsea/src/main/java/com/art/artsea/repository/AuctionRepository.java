package com.art.artsea.repository;

import com.art.artsea.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStartTimeAfter(LocalDateTime dateTime);

    List<Auction> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime start, LocalDateTime end);

    List<Auction> findByEndTimeBefore(LocalDateTime dateTime);

    List<Auction> findByStatus(Auction.AuctionStatus status);

    // Count of Live auctions (now between startTime and endTime)
    @Query("SELECT COUNT(a) FROM Auction a WHERE CURRENT_TIMESTAMP BETWEEN a.startTime AND a.endTime")
    long countLiveAuctions();

    // Count of Upcoming auctions (startTime > now)
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.startTime > CURRENT_TIMESTAMP")
    long countUpcomingAuctions();

    // Count of Past auctions (endTime < now)
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.endTime < CURRENT_TIMESTAMP")
    long countPastAuctions();


    //for buyer's
    @Query("SELECT a FROM Auction a WHERE a.startTime <= CURRENT_TIMESTAMP AND a.endTime >= CURRENT_TIMESTAMP")
    List<Auction> findLiveAuctions();
}

