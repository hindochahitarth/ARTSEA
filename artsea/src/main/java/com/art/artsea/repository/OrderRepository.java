package com.art.artsea.repository;

import com.art.artsea.model.Order;
import com.art.artsea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a specific user
    List<Order> findByUser(User user);

    // Get orders by status (e.g. SELLER_PROCESSING, SELLER_PROCESSED)
    List<Order> findByStatus(String status);

    // Optional: Get orders by user and status
    List<Order> findByUserAndStatus(User user, String status);

    List<Order> findByBid_BidId(Long bidId);

    List<Order> findByUser_UserId(Long userId);

    @Query("SELECT SUM(o.amount) FROM Order o")
    Double getTotalAmount();

    @Query("SELECT MAX(o.amount) FROM Order o")
    Double getHighestAmount();
}
