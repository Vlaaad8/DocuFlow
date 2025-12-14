package com.example;

import com.example.jpa.UserRepository;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User login(String value,String password) {
        return this.userRepository.login(password,value).orElse(null);
    }
}
