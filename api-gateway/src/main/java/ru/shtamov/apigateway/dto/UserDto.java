package ru.shtamov.apigateway.dto;

public class UserDto {
    private String id;
    private String name;
    private String email;

    public UserDto() {
    }

    public UserDto(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static UserDto fallback(String id) {
        return new UserDto(id, "Unknown user", "unavailable@example.com");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
