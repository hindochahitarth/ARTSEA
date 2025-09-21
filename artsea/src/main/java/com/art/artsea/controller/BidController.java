package com.art.artsea.controller;

import com.art.artsea.model.Bid;
import com.art.artsea.model.BidMessage;
import com.art.artsea.model.User;
import com.art.artsea.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class BidController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidService bidService;

    @PostMapping("/bid/{artworkId}")
    public ResponseEntity<?> placeBid(
            @PathVariable Long artworkId,
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        // Get logged-in user
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "User not logged in"
            ));
        }

        if (!"BUYER".equalsIgnoreCase(user.getRole().name())) {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Only buyers can place bids"
            ));
        }

        // Parse bid amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(payload.get("amount").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid bid amount"
            ));
        }

        // Check highest bidder for this artwork only
        Optional<Bid> highestBidOpt = bidService.getHighestBidForArtwork(artworkId);
        if (highestBidOpt.isPresent() && highestBidOpt.get().getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "You are already the highest bidder for this artwork"
            ));
        }

        // Save bid
        BidMessage bidMessage;
        try {
            bidMessage = bidService.saveBid(artworkId, user.getUserId(), amount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }

        // Broadcast via WebSocket
        messagingTemplate.convertAndSend("/topic/auction/" + artworkId, bidMessage);

        return ResponseEntity.ok(Map.of("success", true));
    }


}
