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
    public void saveRelation(int bossID, int subordinateID) {
        User boss = userRepository.findById(bossID).orElseThrow(() -> new RuntimeException("Boss not found"));
        User subordinate = userRepository.findById(subordinateID).orElseThrow(() -> new RuntimeException("Subordinate not found"));
        Relation relation = new Relation();
        relation.setBoss(boss);
        relation.setSubordinate(subordinate);
        relationRepository.save(relation);
    }
}
