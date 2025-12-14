package com.example.web;

import com.example.UserService;
import com.example.login.User;
import lombok.AllArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class LoginController {

    private final UserService userService;

    @PostMapping("login")
    public User login(@RequestBody LoginRequest loginRequest) {
        return  this.userService.login(loginRequest.username(), loginRequest.password());
    }
}
