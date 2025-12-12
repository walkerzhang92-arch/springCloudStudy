package com.walker.userservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id, HttpServletRequest request) {
        String uid = request.getHeader("X-USER-ID");
        String uname = request.getHeader("X-USERNAME");
        return "User-" + id + " (from token uid=" + uid + ", uname=" + uname + ")";
    }



}
