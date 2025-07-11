package org.bluecollar.bluecollar.payment.dto;

public class PaymentRequest {
    private Integer amount;
    private String description;
    
    public PaymentRequest() {}
    
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}