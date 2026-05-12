package com.agnezdei.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalog")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Catalog parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Catalog> children = new ArrayList<>();

    @OneToMany(mappedBy = "catalog")
    private List<BookCopy> bookCopies = new ArrayList<>();

    public Catalog() {}

    public Catalog(String name, Catalog parent) {
        this.name = name;
        this.parent = parent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Catalog getParent() { return parent; }
    public void setParent(Catalog parent) { this.parent = parent; }
    public List<Catalog> getChildren() { return children; }
    public void setChildren(List<Catalog> children) { this.children = children; }
    public List<BookCopy> getBookCopies() { return bookCopies; }
    public void setBookCopies(List<BookCopy> bookCopies) { this.bookCopies = bookCopies; }
}