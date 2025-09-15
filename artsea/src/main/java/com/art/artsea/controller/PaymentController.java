package com.art.artsea.controller;

import com.art.artsea.dto.PaymentSuccessRequest;
import com.art.artsea.model.Bid;
import com.art.artsea.model.Order;
import com.art.artsea.model.User;
import com.art.artsea.repository.OrderRepository;
import com.art.artsea.service.BidService;
import com.art.artsea.service.RazorpayService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private BidService bidService;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Step 1: Save address + create Razorpay order
     */
    @PostMapping("/address")
    @ResponseBody
    public Map<String, Object> saveAddressAndCreateOrder(
            @RequestParam Long bidId,
            @RequestParam String address,
            HttpSession session) throws Exception {

        // Save in session temporarily
        session.setAttribute("paymentBidId", bidId);
        session.setAttribute("deliveryAddress", address);

        Bid bid = bidService.getBidById(bidId).orElseThrow();

        JSONObject orderJson = razorpayService.createOrder(bid.getBidAmount().doubleValue(), "txn_" + bidId);

        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", orderJson.getString("id"));
        response.put("amount", orderJson.getLong("amount"));
        response.put("currency", orderJson.getString("currency"));
        response.put("key", razorpayService.getKeyId());
        response.put("bidId", bidId);

        return response;
    }

    /**
     * Step 2: Payment Success callback
     */
    @PostMapping("/success")
    @ResponseBody
    @Transactional
    public String paymentSuccess(@RequestBody PaymentSuccessRequest request, HttpSession session) {

        Long bidId = (Long) session.getAttribute("paymentBidId");
        String address = (String) session.getAttribute("deliveryAddress");

        Bid bid = bidService.getBidById(bidId).orElseThrow();
        User user = bid.getUser();

        // Create and save order
        Order order = new Order();
        order.setUser(user);
        order.setBid(bid);
        order.setAmount(bid.getBidAmount().doubleValue());
        order.setDeliveryAddress(address);
        order.setStatus("SUCCESS");         // initial status
        order.setPaymentId(request.razorpay_payment_id);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);

        // Clear session
        session.removeAttribute("paymentBidId");
        session.removeAttribute("deliveryAddress");

        return "SUCCESS";
    }

}
