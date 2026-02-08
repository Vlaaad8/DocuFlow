package com.example.jpa;

import com.example.login.Relation;
import org.docx4j.wml.R;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RelationRepository extends JpaRepository<Relation, Integer> {

}
