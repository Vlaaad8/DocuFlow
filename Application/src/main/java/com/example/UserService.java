package com.example;

import com.example.dto.UserDTO;
import com.example.dtoMapper.UserMapper;
import com.example.exceptions.UserException;
import com.example.jpa.UserRepository;
import com.example.login.User;
import com.example.security.CertificatePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificatePort certificatePort;
    private final UserMapper userMapper;

    public UserDTO login(String value, String password) {
        User user =this.userRepository.login(password,value).orElseThrow(() -> new UserException("Invalid credentials"));
            try {
                certificatePort.issueCertificate(user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole().toString(),user.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return userMapper.toUserDTO(user);

    }

}
