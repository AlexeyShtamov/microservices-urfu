package ru.shtamov.apigateway.dto;

import java.math.BigDecimal;

public class ProductDto {
    private String id;
    private String title;
    private BigDecimal price;

    public ProductDto() {
    }

    public ProductDto(String id, String title, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public static ProductDto fallback(String productId) {
        return new ProductDto(productId, "Unknown product", BigDecimal.ZERO);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
