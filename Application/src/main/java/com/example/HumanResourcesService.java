package com.example;

import com.example.jpa.RelationRepository;
import com.example.jpa.UserRepository;
import com.example.login.Relation;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
//TODO sa stochez undeva dependentele intre roluri , iar clasa Relationship sa fie pentru persoane practic , cand o sa fac un chain sa verific daca el este posibil dupa regulile ierarhice, sa nu las HR sa faca o prostie daca regulile ierarhice contrazic ce face el
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
