package com.example;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Template {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private TemplateCateogry category;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private boolean required;
    @Column(nullable = false)
    private TemplateStatus status;
    @Column(nullable = false)
    private String storagePath;


}
