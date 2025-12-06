package ru.shtamov.api_gateway.model;

import java.util.List;

public class ProfileResponse {
    private UserSummary user;
    private List<OrderSummary> orders;
    private boolean cached;

    public UserSummary getUser() {
        return user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

    public List<OrderSummary> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderSummary> orders) {
        this.orders = orders;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
