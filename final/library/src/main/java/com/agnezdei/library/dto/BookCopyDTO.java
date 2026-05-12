package com.agnezdei.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookCopyDTO {
    private Long id;
    @NotBlank(message = "Инвентарный номер обязателен")
    private String inventoryNumber;
    @NotNull(message = "Статус обязателен")
    private String status;
    @NotNull(message = "ID издания обязателен")
    private Long editionId;
    @NotNull(message = "ID каталога обязателен")
    private Long catalogId;

    public BookCopyDTO() {}

    public BookCopyDTO(Long id, String inventoryNumber, String status, Long editionId, Long catalogId) {
        this.id = id;
        this.inventoryNumber = inventoryNumber;
        this.status = status;
        this.editionId = editionId;
        this.catalogId = catalogId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getEditionId() { return editionId; }
    public void setEditionId(Long editionId) { this.editionId = editionId; }

    public Long getCatalogId() { return catalogId; }
    public void setCatalogId(Long catalogId) { this.catalogId = catalogId; }
}