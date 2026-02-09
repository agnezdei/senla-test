package com.agnezdei.hotelmvc.mapper;

import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    
    public static BookingDTO toDTO(Booking booking) {
        if (booking == null) return null;
        
        String guestName = "";
        String roomNumber = "";
        
        if (booking.getGuest() != null) {
            guestName = booking.getGuest().getName();
        }
        
        if (booking.getRoom() != null) {
            roomNumber = booking.getRoom().getNumber();
        }

        String checkInDateStr = booking.getCheckInDate() != null ? 
                               booking.getCheckInDate().toString() : "";
        String checkOutDateStr = booking.getCheckOutDate() != null ? 
                                booking.getCheckOutDate().toString() : "";
        
        return new BookingDTO(
            booking.getId(),
            guestName,
            roomNumber,
            checkInDateStr,
            checkOutDateStr,
            booking.isActive()
        );
    }
    
    public static Booking toEntity(BookingDTO dto) {
        if (dto == null) return null;
        
        Booking booking = new Booking();
        booking.setId(dto.getId());
        
        if (dto.getCheckInDate() != null && !dto.getCheckInDate().isEmpty()) {
            booking.setCheckInDate(LocalDate.parse(dto.getCheckInDate()));
        }
        
        if (dto.getCheckOutDate() != null && !dto.getCheckOutDate().isEmpty()) {
            booking.setCheckOutDate(LocalDate.parse(dto.getCheckOutDate()));
        }
        
        booking.setActive(dto.isActive());
        return booking;
    }
    
    public static void updateEntityFromDTO(Booking booking, BookingDTO dto) {
        if (dto == null) return;
        
        if (dto.getCheckInDate() != null && !dto.getCheckInDate().isEmpty()) {
            booking.setCheckInDate(LocalDate.parse(dto.getCheckInDate()));
        }
        
        if (dto.getCheckOutDate() != null && !dto.getCheckOutDate().isEmpty()) {
            booking.setCheckOutDate(LocalDate.parse(dto.getCheckOutDate()));
        }
        
        booking.setActive(dto.isActive());
    }
    
    public static List<BookingDTO> toDTOList(List<Booking> bookings) {
        return bookings.stream()
                      .map(BookingMapper::toDTO)
                      .collect(Collectors.toList());
    }
}