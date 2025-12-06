package ru.shtamov.apigateway.dto;

import java.math.BigDecimal;

public class OrderDto {
    private String id;
    private String productId;
    private int quantity;
    private BigDecimal total;

    public OrderDto() {
    }

    public OrderDto(String id, String productId, int quantity, BigDecimal total) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
