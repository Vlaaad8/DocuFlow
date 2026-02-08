package com.example.approval;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "approval_chains")
@Getter
@Setter
public class ApprovalChain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "approvalChain", cascade = CascadeType.ALL)
    //@OrderBy("stepOrder ASC")
    private List<ApprovalStep> steps;


}
