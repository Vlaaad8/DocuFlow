package com.example.jpa;

import com.example.login.Relation;
import com.example.login.Role;
import com.example.login.User;
import org.docx4j.wml.R;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {

    Relation findBySubordinate_IdAndBoss_Role(int subordinateId, Role bossRole);

    @Query("SELECT r.boss FROM Relation r WHERE r.subordinate.id = :subordinateId AND r.boss.role = :bossRole")
    User findBossBySubordinate_IdAndBoss_Role(@Param("subordinateId") int subordinateId, @Param("bossRole") Role bossRole);

}
