package com.art.artsea.repository;

import com.art.artsea.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    // Find all artworks by user ID
    List<Artwork> findByUserUserId(Long userId);

    // Find all artworks for a specific auction
    List<Artwork> findByAuctionAuctionId(Long auctionId);

    // Find artwork by ID with user and auction details
    @Query("SELECT a FROM Artwork a JOIN FETCH a.user JOIN FETCH a.auction WHERE a.artworkId = :artworkId")
    Optional<Artwork> findByIdWithDetails(@Param("artworkId") Long artworkId);

    // Check if artwork exists for user in auction (to prevent duplicate submissions)
    boolean existsByUserUserIdAndAuctionAuctionId(Long userId, Long auctionId);
}