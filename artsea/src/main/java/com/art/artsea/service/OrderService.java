package com.art.artsea.service;

import com.art.artsea.model.Bid;
import com.art.artsea.model.Order;
import com.art.artsea.model.User;
import com.art.artsea.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(User user, Bid bid, double amount, String paymentId, String deliveryAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setBid(bid);
        order.setAmount(amount);
        order.setPaymentId(paymentId);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("SELLER_PROCESSING"); // payment done, seller must process
        return orderRepository.save(order);
    }

    public void updateTracking(Long orderId, String trackingId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setTrackingId(trackingId);
        order.setStatus("SELLER_PROCESSED");
        orderRepository.save(order);
    }
}
