package org.bluecollar.bluecollar.payment.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}