package com.tokenbroker.auth.logic.model;

public class UserModel {
    private String tenantName;
    private String username;
    private String password;
    private String role;

    public UserModel(String tenantName, String username, String password, String role) {
        this.tenantName = tenantName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean match(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}