package ru.shtamov.apigateway.dto;

public class OrderWithProductDto {
    private OrderDto order;
    private ProductDto product;

    public OrderWithProductDto() {
    }

    public OrderWithProductDto(OrderDto order, ProductDto product) {
        this.order = order;
        this.product = product;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }
}
