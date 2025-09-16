package com.brand.artifact.constant;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    VNPAY("VNPay"),
    MOMO("MoMo"),
    ZALOPAY("ZaloPay"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    CREDIT_CARD("Thẻ tín dụng");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}