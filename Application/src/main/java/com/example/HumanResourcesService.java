package com.example;

import com.example.exceptions.RelationException;
import com.example.jpa.RelationRepository;
import com.example.jpa.UserRepository;
import com.example.login.Relation;
import com.example.login.Role;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class HumanResourcesService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;

    private final Map<Role, Integer> hierarchyLevels = Map.of(
            Role.CEO, 0,
            Role.Manager, 1,
            Role.HumanResources, 2,
            Role.IT, 2,
            Role.Finance, 2,
            Role.Law, 2,
            Role.Marketing, 3,
            Role.Sales, 3,
            Role.Employee, 4
    );

    public List<Relation> getAllRelations() {
        return relationRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void saveRelation(int bossID, int subordinateID) {
        User boss = userRepository.findById(bossID).orElseThrow(() -> new RelationException("Boss not found"));
        User subordinate = userRepository.findById(subordinateID).orElseThrow(() -> new RelationException("Subordinate not found"));

        if (hierarchyLevels.get(boss.getRole()) > hierarchyLevels.get(subordinate.getRole())) {
            throw new RelationException("Boss must have a higher role than subordinate");
        }

        if(boss.getRole() == subordinate.getRole()) {
            throw new RelationException("Boss and subordinate cannot have the same role");
        }

        if(boss.getRole() == Role.Employee) {
            throw new RelationException("Employee cannot be a boss");
        }

        Relation relation = new Relation();
        relation.setBoss(boss);
        relation.setSubordinate(subordinate);
        relationRepository.save(relation);
    }
}
