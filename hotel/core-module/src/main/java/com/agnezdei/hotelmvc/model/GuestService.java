package com.agnezdei.hotelmvc.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    private LocalDate serviceDate;
    
    public GuestService() {
    }
    
    public GuestService(Guest guest, Service service, LocalDate serviceDate) {
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

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }
    
    @Override
    public String toString() {
        return "GuestService [id=" + id + ", guest=" + (guest != null ? guest.getName() : "null")
                + ", service=" + (service != null ? service.getName() : "null")
                + ", serviceDate=" + serviceDate + "]";
    }
}