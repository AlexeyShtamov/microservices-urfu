package ru.shtamov.apigateway.dto;

import java.util.List;

public class ProfileResponse {
    private UserDto user;
    private List<OrderWithProductDto> orders;

    public ProfileResponse() {
    }

    public ProfileResponse(UserDto user, List<OrderWithProductDto> orders) {
        this.user = user;
        this.orders = orders;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<OrderWithProductDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderWithProductDto> orders) {
        this.orders = orders;
    }
}
