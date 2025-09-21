package com.art.artsea.repository;

import com.art.artsea.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.artwork.artworkId = :artworkId")
    Optional<Double> findHighestBidByArtworkId(@Param("artworkId") Long artworkId);

//    Optional<Bid> findTopByArtwork_ArtworkIdOrderByBidAmountDesc(Long artworkId);
// Get highest bid for a specific artwork
Optional<Bid> findTopByArtwork_ArtworkIdOrderByBidAmountDescBidTimeDesc(Long artworkId);


    List<Bid> findByUserUserId(Long userId);

    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.artwork.artworkId = :artworkId")
    Optional<Double> findMaxBidAmountByArtworkId(@Param("artworkId") Long artworkId);

    @Query("SELECT b FROM Bid b WHERE b.artwork.artworkId = :artworkId ORDER BY b.bidAmount DESC")
    List<Bid> findTopBidByArtworkId(Long artworkId);
}
