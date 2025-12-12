package com.walker.authservice.dto;

/**
 * 登录请求参数
 */
public class LoginRequest {

    private String username;
    private String password;

    /**
     * Jackson 反序列化需要无参构造
     */
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
