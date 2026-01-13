package com.agnezdei.hotelmvc.model;

import java.time.LocalDate;

public class BookingService {
    private Long id;
    private Booking booking;
    private Service service;
    private LocalDate serviceDate;
    
    public BookingService() {
    }
    
    public BookingService(Booking booking, Service service, LocalDate serviceDate) {
        this.booking = booking;
        this.service = service;
        this.serviceDate = serviceDate;
    }
    
    public BookingService(Long bookingId, Long serviceId, LocalDate serviceDate) {
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    
    @Override
    public String toString() {
        return "BookingService [id=" + id + ", service=" + service + ", serviceDate=" + serviceDate + "]";
    }
}