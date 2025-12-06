package ru.shtamov.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    private String userServiceUrl;
    private String orderServiceUrl;
    private String productServiceUrl;

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public String getOrderServiceUrl() {
        return orderServiceUrl;
    }

    public void setOrderServiceUrl(String orderServiceUrl) {
        this.orderServiceUrl = orderServiceUrl;
    }

    public String getProductServiceUrl() {
        return productServiceUrl;
    }

    public void setProductServiceUrl(String productServiceUrl) {
        this.productServiceUrl = productServiceUrl;
    }
}
