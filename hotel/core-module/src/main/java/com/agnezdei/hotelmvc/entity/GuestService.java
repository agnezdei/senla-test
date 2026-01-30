package com.agnezdei.hotelmvc.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "guest_service")
public class GuestService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(name = "service_date", nullable = false)
    private String serviceDate;
    
    public GuestService() {
    }
    
    public GuestService(Guest guest, Service service, String serviceDate) {
        this.guest = guest;
        this.service = service;
        this.serviceDate = serviceDate;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }
    
    public LocalDate getServiceDateAsLocalDate() {
        try {
            return LocalDate.parse(serviceDate);
        } catch (Exception e) {
            return null;
        }
    }
}