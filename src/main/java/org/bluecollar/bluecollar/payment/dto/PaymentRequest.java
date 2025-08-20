package org.bluecollar.bluecollar.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer amount;
    private String description;
}