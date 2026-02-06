package com.example.web;

import com.example.HumanResourcesService;
import com.example.login.Relation;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class HumanResourcesController {

    private final HumanResourcesService humanResourcesService;


    @GetMapping("hr/users")
    public List<User> getAllUsers() {
        return humanResourcesService.getAllUsers();
    }

    @GetMapping("hr/relations")
    public List<Relation> getAllRelations() {
        return humanResourcesService.getAllRelations();
    }
    @PostMapping("hr/relation")
    public void saveRelation(@RequestParam("bossID") int bossID, @RequestParam("subordinateID") int subordinateID) {
        humanResourcesService.saveRelation(bossID, subordinateID);
    }
}
