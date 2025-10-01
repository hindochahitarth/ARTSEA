package com.art.artsea.controller;

import com.art.artsea.model.Order;
import com.art.artsea.model.User;
import com.art.artsea.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seller-orders")
public class SellerOrderController {

    @Autowired
    private OrderRepository orderRepository;

    // Show pending orders (Payment done but not fulfilled yet)
    @GetMapping("/pending")
    public String viewPendingOrders(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/seller";
        }

        if (user instanceof User loggedInUser) {
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }

            List<Order> pendingOrders = orderRepository.findAll()
                    .stream()
                    .filter(order ->
                            order.getBid().getArtwork().getUser().getUserId()
                                    .equals(loggedInUser.getUserId())
                                    && "SUCCESS".equalsIgnoreCase(order.getStatus())
                    )
                    .toList();

            model.addAttribute("orders", pendingOrders);
        }

        return "seller/order-fulfillment"; // shows pending orders
    }

    // Show processed orders (Sales history)
    @GetMapping("/processed")
    public String viewProcessedOrders(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/seller";
        }

        if (user instanceof User loggedInUser) {
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }

            List<Order> processedOrders = orderRepository.findAll()
                    .stream()
                    .filter(order ->
                            order.getBid().getArtwork().getUser().getUserId()
                                    .equals(loggedInUser.getUserId())
                                    && "PROCESSED".equalsIgnoreCase(order.getStatus())
                    )
                    .toList();

            model.addAttribute("orders", processedOrders);
        }

        return "seller/sales"; // shows processed orders
    }

    // Mark order as processed
    @PostMapping("/process")
    public String processOrder(@RequestParam Long orderId,
                               @RequestParam String trackingId,
                               HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null || !(user instanceof User loggedInUser)) {
            return "redirect:/seller";
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));

        // Ensure this seller owns the artwork
        if (!order.getBid().getArtwork().getUser().getUserId()
                .equals(loggedInUser.getUserId())) {
            throw new SecurityException("You cannot process this order!");
        }

        order.setStatus("PROCESSED");
        order.setTrackingId(trackingId);
        orderRepository.save(order);

        // Stay on same page (order-fulfillment.html)
        return "redirect:/seller-orders/pending";
    }
}
