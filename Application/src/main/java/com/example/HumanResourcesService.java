package com.example;

import com.example.jpa.RelationRepository;
import com.example.jpa.UserRepository;
import com.example.login.Relation;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HumanResourcesService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;

    public List<Relation> getAllRelations() {
        return relationRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
