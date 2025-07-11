package org.bluecollar.bluecollar.payment.controller;

import org.bluecollar.bluecollar.payment.dto.*;
import org.bluecollar.bluecollar.payment.model.Payment;
import org.bluecollar.bluecollar.payment.service.PaymentService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private SessionService sessionService;
    
    @PostMapping("/create")
    public BlueCollarApiResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request,
                                                               @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            PaymentResponse response = paymentService.createPayment(request, customerId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @PostMapping("/verify")
    public BlueCollarApiResponse<String> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        try {
            String message = paymentService.verifyPayment(request);
            return new BlueCollarApiResponse<>(message, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(e.getMessage(), 400);
        }
    }
    
    @GetMapping("/history")
    public BlueCollarApiResponse<List<Payment>> getPaymentHistory(@RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            List<Payment> payments = paymentService.getCustomerPayments(customerId);
            return new BlueCollarApiResponse<>(payments, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @PostMapping("/retry/{paymentId}")
    public BlueCollarApiResponse<String> retryPayment(@PathVariable String paymentId,
                                                     @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            String message = paymentService.retryFailedPayment(paymentId, customerId);
            return new BlueCollarApiResponse<>(message, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(e.getMessage(), 400);
        }
    }
    
    @GetMapping("/status/{paymentId}")
    public BlueCollarApiResponse<String> getPaymentStatus(@PathVariable String paymentId,
                                                         @RequestHeader("Session-Token") String sessionToken) {
        try {
            String customerId = sessionService.validateSession(sessionToken).getCustomerId();
            String status = paymentService.getPaymentStatus(paymentId, customerId);
            return new BlueCollarApiResponse<>(status, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(e.getMessage(), 400);
        }
    }
}