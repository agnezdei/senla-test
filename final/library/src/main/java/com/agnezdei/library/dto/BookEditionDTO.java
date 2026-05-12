package com.agnezdei.library.dto;

import jakarta.validation.constraints.NotBlank;

public class BookEditionDTO {
    private Long id;
    @NotBlank(message = "Название книги не может быть пустым")
    private String title;
    @NotBlank(message = "Автор не может быть пустым")
    private String author;
    private String isbn;
    private Integer publicationYear;
    private String description;

    public BookEditionDTO() {}

    public BookEditionDTO(Long id, String title, String author,
                      String isbn, Integer publicationYear, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
