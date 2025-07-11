package org.bluecollar.bluecollar.payment.service;

import org.bluecollar.bluecollar.payment.dto.*;
import org.bluecollar.bluecollar.payment.model.Payment;
import org.bluecollar.bluecollar.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.Utils;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    private int failureCount = 0;
    private LocalDateTime lastFailureTime;
    private final int FAILURE_THRESHOLD = 3;
    private final long CIRCUIT_BREAKER_TIMEOUT = 300000; // 5 minutes
    
    private RazorpayClient getRazorpayClient() throws Exception {
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }
    
    public PaymentResponse createPayment(PaymentRequest request, String customerId) {
        // Circuit breaker check
        if (isCircuitBreakerOpen()) {
            return createFallbackPayment(request, customerId);
        }
        
        try {
            RazorpayClient razorpay = getRazorpayClient();
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount() * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());
            
            Order order = razorpay.orders.create(orderRequest);
            
            Payment payment = new Payment();
            payment.setCustomerId(customerId);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setAmount(request.getAmount() * 100);
            payment.setDescription(request.getDescription());
            
            Payment saved = paymentRepository.save(payment);
            resetCircuitBreaker(); // Reset on success
            
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(saved.getId());
            response.setRazorpayOrderId(saved.getRazorpayOrderId());
            response.setAmount(saved.getAmount());
            response.setCurrency(saved.getCurrency());
            response.setStatus(saved.getStatus());
            response.setRazorpayKey(razorpayKeyId);
            
            return response;
        } catch (Exception e) {
            recordFailure();
            return createFallbackPayment(request, customerId);
        }
    }
    
    public String verifyPayment(PaymentVerifyRequest request) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());
            
            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            
            if (isValid) {
                Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                        .orElseThrow(() -> new RuntimeException("Payment not found"));
                
                payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
                payment.setRazorpaySignature(request.getRazorpaySignature());
                payment.setStatus("PAID");
                
                paymentRepository.save(payment);
                return "Payment verified successfully";
            } else {
                // Fallback: Mark for manual verification
                Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                        .orElse(null);
                if (payment != null) {
                    payment.setStatus("VERIFICATION_PENDING");
                    paymentRepository.save(payment);
                }
                throw new RuntimeException("Invalid payment signature - marked for manual verification");
            }
        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }
    
    public List<Payment> getCustomerPayments(String customerId) {
        return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
    
    private boolean isCircuitBreakerOpen() {
        if (failureCount >= FAILURE_THRESHOLD) {
            if (lastFailureTime != null && 
                LocalDateTime.now().isBefore(lastFailureTime.plusNanos(CIRCUIT_BREAKER_TIMEOUT * 1000000))) {
                return true;
            } else {
                // Reset circuit breaker after timeout
                failureCount = 0;
                lastFailureTime = null;
            }
        }
        return false;
    }
    
    private void recordFailure() {
        failureCount++;
        lastFailureTime = LocalDateTime.now();
    }
    
    private void resetCircuitBreaker() {
        failureCount = 0;
        lastFailureTime = null;
    }
    
    private PaymentResponse createFallbackPayment(PaymentRequest request, String customerId) {
        // Create payment record without Razorpay order
        Payment payment = new Payment();
        payment.setCustomerId(customerId);
        payment.setRazorpayOrderId("FALLBACK_" + System.currentTimeMillis());
        payment.setAmount(request.getAmount() * 100);
        payment.setDescription(request.getDescription());
        payment.setStatus("FALLBACK_CREATED");
        
        Payment saved = paymentRepository.save(payment);
        
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(saved.getId());
        response.setRazorpayOrderId(saved.getRazorpayOrderId());
        response.setAmount(saved.getAmount());
        response.setCurrency(saved.getCurrency());
        response.setStatus("FALLBACK_MODE");
        response.setRazorpayKey(razorpayKeyId);
        
        return response;
    }
    
    public String retryFailedPayment(String paymentId, String customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!payment.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if ("FAILED".equals(payment.getStatus()) || "FALLBACK_CREATED".equals(payment.getStatus())) {
            try {
                // Try to create new Razorpay order
                RazorpayClient razorpay = getRazorpayClient();
                
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", payment.getAmount());
                orderRequest.put("currency", payment.getCurrency());
                orderRequest.put("receipt", "retry_" + System.currentTimeMillis());
                
                Order order = razorpay.orders.create(orderRequest);
                
                payment.setRazorpayOrderId(order.get("id"));
                payment.setStatus("CREATED");
                paymentRepository.save(payment);
                
                return "Payment retry successful. New order ID: " + order.get("id");
            } catch (Exception e) {
                payment.setStatus("RETRY_FAILED");
                paymentRepository.save(payment);
                throw new RuntimeException("Retry failed: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Payment cannot be retried in current status: " + payment.getStatus());
        }
    }
    
    public String getPaymentStatus(String paymentId, String customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!payment.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // If payment is in CREATED status for more than 30 minutes, mark as FAILED
        if ("CREATED".equals(payment.getStatus()) && 
            payment.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(30))) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }
        
        return payment.getStatus();
    }
}