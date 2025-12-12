package com.walker.authservice.controller;

import com.walker.authservice.dto.LoginRequest;
import com.walker.authservice.dto.LoginResponse;
import com.walker.authservice.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {

        // 先写死，后面接数据库
        if ("admin".equals(req.getUsername()) && "123456".equals(req.getPassword())) {
            String token = jwtUtil.createToken(1L, "admin");

            return new LoginResponse(token);
        }

        throw new RuntimeException("账号或密码错误");
    }
}
