package org.bluecollar.bluecollar.payment.dto;

public class PaymentResponse {
    private String paymentId;
    private String razorpayOrderId;
    private Integer amount;
    private String currency;
    private String status;
    private String razorpayKey;
    
    public PaymentResponse() {}
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRazorpayKey() { return razorpayKey; }
    public void setRazorpayKey(String razorpayKey) { this.razorpayKey = razorpayKey; }
}