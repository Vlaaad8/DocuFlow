package com.example;

import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.security.CertificatePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificatePort certificatePort;

    public User login(String value,String password) {
        User user =this.userRepository.login(password,value).orElse(null);
        if(user != null){
            try {
                certificatePort.issueCertificate(user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole().toString(),user.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return user;
        }
        return null;
    }
}
