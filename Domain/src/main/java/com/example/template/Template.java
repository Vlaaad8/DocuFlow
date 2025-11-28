package com.example.template;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.temporal.IsoFields;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="templates")
public class Template {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateCategory category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateStatus status;
    @Column(nullable = false)
    private String storagePath;
    @ManyToMany
    @JoinTable(name = "template_fields",joinColumns = @JoinColumn (name = "template_id"),inverseJoinColumns = @JoinColumn(name="field_id"))
    private Set<Field> fields = new HashSet<>();

    public Template(String name, TemplateCategory category, TemplateStatus status, String storagePath, Set<Field> fields ) {
        this.name = name;
        this.category = category;
        this.status = status;
        this.storagePath = storagePath;
        this.fields = fields;
    }
}
