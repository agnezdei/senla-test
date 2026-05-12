package com.agnezdei.library.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class CatalogDTO {
    private Long id;
    @NotBlank(message = "Название каталога не может быть пустым")
    private String name;
    private Long parentId;
    private List<CatalogDTO> children;

    public CatalogDTO() {}

    public CatalogDTO(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public List<CatalogDTO> getChildren() { return children; }
    public void setChildren(List<CatalogDTO> children) { this.children = children; }
}
