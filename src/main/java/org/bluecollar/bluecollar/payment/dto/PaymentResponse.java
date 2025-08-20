package org.bluecollar.bluecollar.payment.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentId;
    private String razorpayOrderId;
    private Integer amount;
    private String currency;
    private String status;
    private String razorpayKey;
}