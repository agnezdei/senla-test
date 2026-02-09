package com.agnezdei.hotelmvc.dto;

public class GuestServiceDTO {
    private Long id;
    private String guestName;
    private String serviceName;
    private String serviceCategory;
    private String serviceDate;
    
    public GuestServiceDTO() {}
    
    public GuestServiceDTO(Long id, String guestName, String serviceName, 
                          String serviceCategory, String serviceDate) {
        this.id = id;
        this.guestName = guestName;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.serviceDate = serviceDate;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    
    public String getServiceDate() { return serviceDate; }
    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
    
    @Override
    public String toString() {
        return "Услуга для гостя [id=" + id + ", гость=" + guestName 
               + ", услуга=" + serviceName + " (" + serviceCategory + "), дата=" + serviceDate + "]";
    }
}