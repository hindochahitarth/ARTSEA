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

    @Query("SELECT a FROM Artwork a WHERE a.user.userId = :userId ORDER BY a.createdAt DESC")
    List<Artwork> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // Count approved artworks
    @Query("SELECT COUNT(a) FROM Artwork a WHERE a.status = 'APPROVED'")
    long countApprovedArtworks();

    // Count pending artworks
    @Query("SELECT COUNT(a) FROM Artwork a WHERE a.status = 'PENDING'")
    long countPendingArtworks();

    //For seller
    @Query("SELECT COUNT(DISTINCT a.auction.auctionId) FROM Artwork a WHERE a.user.userId = :sellerId")
    long countAppliedAuctions(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(a) FROM Artwork a " +
            "WHERE a.status = 'APPROVED' " +
            "AND a.auction.startTime <= CURRENT_TIMESTAMP " +
            "AND a.auction.endTime >= CURRENT_TIMESTAMP " +
            "AND a.user.userId = :sellerId")
    long countLiveArtworks(@Param("sellerId") Long sellerId);


    @Query("SELECT COUNT(a) FROM Artwork a " +
            "WHERE a.status = 'APPROVED' " +
            "AND a.user.userId = :sellerId")
    long countSellerApprovedArtworks(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(a) FROM Artwork a " +
            "WHERE a.status = 'PENDING' " +
            "AND a.user.userId = :sellerId")
    long countSellerPendingArtworks(@Param("sellerId") Long sellerId);


    @Query("SELECT a FROM Artwork a WHERE a.auction.auctionId = :auctionId AND a.status = 'APPROVED'")
    List<Artwork> findApprovedArtworksByAuctionId(@Param("auctionId") Long auctionId);

    //for user
    @Query("SELECT COUNT(ar) FROM Artwork ar " +
            "WHERE ar.auction.auctionId = :auctionId " +
            "AND ar.status = 'APPROVED'")
    long countApprovedArtworksByAuctionId(Long auctionId);

//    @Query("SELECT a FROM Artwork a WHERE a.auction.auctionId = :auctionId AND a.status = 'APPROVED'")
//    List<Artwork> findByAuctionIdAndStatus(@Param("auctionId") Long auctionId, @Param("status") String status);

    @Query("SELECT a FROM Artwork a WHERE a.auction.auctionId = :auctionId AND a.status = :status")
    List<Artwork> findByAuctionIdAndStatus(@Param("auctionId") Long auctionId,
                                           @Param("status") Artwork.ArtworkStatus status);


}
