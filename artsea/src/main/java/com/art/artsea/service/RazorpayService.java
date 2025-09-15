package com.art.artsea.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws RazorpayException {
        client = new RazorpayClient(keyId, keySecret);
    }

    public JSONObject createOrder(Double amount, String receipt) throws RazorpayException {
        long amountInPaise = Math.round(amount * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        // Create Razorpay order
        Order order = client.orders.create(orderRequest);

        JSONObject orderJson = new JSONObject();
        orderJson.put("id", order.get("id").toString());
        orderJson.put("amount", amountInPaise);
        orderJson.put("currency", "INR");

        return orderJson;
    }

    public String getKeyId() {
        return keyId;
    }
}
