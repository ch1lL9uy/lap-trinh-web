package com.brand.artifact.constant;

public enum PaymentStatus {
    PENDING("Đang chờ thanh toán"),
    SUCCESS("Thanh toán thành công"), 
    FAILED("Thanh toán thất bại"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền"),
    EXPIRED("Hết hạn");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}