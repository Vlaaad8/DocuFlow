package com.example.email;


import com.example.login.User;

public interface EmailPort {
    void sendEmail(String file,String email,String firstName,String lastName);
    void sendRegisterEmail(User user);

}
