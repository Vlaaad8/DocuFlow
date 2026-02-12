package com.example.template;

import com.example.approval.ApprovalChain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "templates")
public class Template {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateCategory category;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String storagePath;
    @ManyToMany
    @JoinTable(name = "template_fields", joinColumns = @JoinColumn(name = "template_id"), inverseJoinColumns = @JoinColumn(name = "field_id"))
    private Set<Field> fields = new HashSet<>();

    @ManyToOne
    private ApprovalChain approvalChain;

    public Template(String name, TemplateCategory category, String description, String storagePath, Set<Field> fields,ApprovalChain approvalChain) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.storagePath = storagePath;
        this.fields = fields;
        this.approvalChain = approvalChain;
    }
}
