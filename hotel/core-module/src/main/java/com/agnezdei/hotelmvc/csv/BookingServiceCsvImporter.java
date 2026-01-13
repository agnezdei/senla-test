package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.BookingService;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.repository.impl.BookingRepository;
import com.agnezdei.hotelmvc.repository.impl.BookingServiceRepository;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;

public class BookingServiceCsvImporter {
    @Inject
    private BookingRepository bookingDAO;
    @Inject
    private ServiceRepository serviceDAO;
    @Inject
    private BookingServiceRepository bookingServiceDAO;

    public BookingServiceCsvImporter() {
    }

    public String importBookingServices(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        
        System.out.println("\n=== Начало импорта услуг бронирований ===");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                
                try {
                    String[] data = line.split(",");
                    
                    if (data.length < 3) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 3)");
                        continue;
                    }
                    
                    Long bookingId = Long.parseLong(data[0].trim());
                    Long serviceId = Long.parseLong(data[1].trim());
                    LocalDate serviceDate = LocalDate.parse(data[2].trim());
                    
                    Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
                    if (bookingOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Бронирование с ID " + bookingId + " не найдено");
                        continue;
                    }
                    
                    Optional<Service> serviceOpt = serviceDAO.findById(serviceId);
                    if (serviceOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Услуга с ID " + serviceId + " не найдена");
                        continue;
                    }
                    
                    Booking booking = bookingOpt.get();
                    Service service = serviceOpt.get();
                    
                    if (serviceDate.isBefore(booking.getCheckInDate()) || 
                        serviceDate.isAfter(booking.getCheckOutDate())) {
                        errors.add("Строка " + lineNum + ": Дата услуги " + serviceDate + 
                                 " вне периода бронирования (" + booking.getCheckInDate() + 
                                 " - " + booking.getCheckOutDate() + ")");
                        continue;
                    }
                    
                    BookingService bookingService = new BookingService();
                    bookingService.setBooking(booking);
                    bookingService.setService(service);
                    bookingService.setServiceDate(serviceDate);
                    
                    bookingServiceDAO.save(bookingService);
                    imported++;
                    
                } catch (Exception e) {
                    errors.add("Строка " + lineNum + ": " + e.getMessage() + " - " + line);
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }
        
        return String.format("Импорт услуг бронирований завершен: %d добавлено. Ошибок: %d", 
                            imported, errors.size());
    }
}