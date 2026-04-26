package com.example;

import com.example.dto.UserDTO;
import com.example.dtoMapper.UserMapper;
import com.example.email.EmailPort;
import com.example.exceptions.UserException;
import com.example.jpa.UserRepository;
import com.example.login.Role;
import com.example.login.User;
import com.example.security.CertificatePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificatePort certificatePort;
    private final UserMapper userMapper;
    private final EmailPort emailPort;

    public UserDTO login(String value, String password) {
        User user =this.userRepository.login(password,value).orElseThrow(() -> new UserException("Invalid credentials"));
        return userMapper.toUserDTO(user);

    }

    @Transactional
    public void register(String firstName, String lastName, String email, String password, String username, String role){
         if(userRepository.existsByEmail(email)) {
             throw new UserException("Email already exists");
         }
         if(userRepository.existsByUsername(username)) {
             throw new UserException("Username already exists");
         }
         if (userRepository.existsByRole(Role.CEO) && Role.valueOf(role) == Role.CEO) {
             throw new UserException("A CEO already exists");
         }
         User user = new User();
         user.setFirstName(firstName);
         user.setLastName(lastName);
         user.setEmail(email);
         user.setPassword(password);
         user.setUsername(username);
         user.setRole(Role.valueOf(role));
         user.setCertificatePassword(password);

         int userID = userRepository.saveAndFlush(user).getId();
        try {
            certificatePort.issueCertificate(user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole().toString(),userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
         this.emailPort.sendRegisterEmail(user);
    }

}
