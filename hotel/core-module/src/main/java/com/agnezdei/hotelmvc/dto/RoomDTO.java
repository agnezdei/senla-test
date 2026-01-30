package com.agnezdei.hotelmvc.dto;

public class RoomDTO {
    private Long id;
    private String number;
    private String type;
    private Double price;
    private String status;
    private Integer capacity;
    private Integer stars;
    
    public RoomDTO() {}
    
    public RoomDTO(Long id, String number, String type, Double price, 
                   String status, Integer capacity, Integer stars) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = status;
        this.capacity = capacity;
        this.stars = stars;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    
    @Override
    public String toString() {
        return "Номер " + number + " (" + type + ") - " + price + " руб. [" + status +
                "], Вместимость: " + capacity + ", Звёзды: " + stars;
    }
}