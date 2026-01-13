package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.repository.impl.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HotelReporter {
    @Inject
    private RoomRepository roomDAO;
    
    @Inject
    private GuestRepository guestDAO;
    
    @Inject
    private ServiceRepository serviceDAO;
    
    @Inject
    private BookingRepository bookingDAO;

    @Inject
    private BookingServiceRepository bookingServiceDAO;
    
    private static class PricedItem {
        String name;
        String category;
        double price;
        
        PricedItem(String name, String category, double price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    public HotelReporter() {
    }

    public List<Room> getRoomsSortedByPrice() {
        try {
            return roomDAO.findAll().stream()
                .sorted(Comparator.comparing(Room::getPrice))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByCapacity() {
        try {
            return roomDAO.findAll().stream()
                .sorted(Comparator.comparing(Room::getCapacity))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByStars() {
        try {
            return roomDAO.findAll().stream()
                .sorted(Comparator.comparing(Room::getStars))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Room> getAvailableRooms() {
        try {
            return roomDAO.findAvailableRooms();
        } catch (Exception e) {
            System.err.println("Ошибка при получении доступных комнат: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByPrice() {
        return getAvailableRooms().stream()
            .sorted(Comparator.comparing(Room::getPrice))
            .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsSortedByCapacity() {
        return getAvailableRooms().stream()
            .sorted(Comparator.comparing(Room::getCapacity))
            .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsSortedByStars() {
        return getAvailableRooms().stream()
            .sorted(Comparator.comparing(Room::getStars))
            .collect(Collectors.toList());
    }

    public int getTotalAvailableRooms() {
        return getAvailableRooms().size();
    }

    public List<Booking> getGuestsAndRoomsSortedByName() {
        try {
            return bookingDAO.findActiveBookings().stream()
                .sorted(Comparator.comparing(b -> b.getGuest().getName()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Ошибка при получении бронирований: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        try {
            return bookingDAO.findActiveBookings().stream()
                .sorted(Comparator.comparing(Booking::getCheckOutDate))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Ошибка при получении бронирований: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        try {
            List<Room> allRooms = roomDAO.findAll();
            List<Room> availableRooms = new ArrayList<>();
            
            for (Room room : allRooms) {
                if (isRoomAvailableOnDate(room, date)) {
                    availableRooms.add(room);
                }
            }
            
            return availableRooms;
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат на дату: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private boolean isRoomAvailableOnDate(Room room, LocalDate date) {
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            return false;
        }
        
        try {
            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            for (Booking booking : bookings) {
                if (booking.isActive() && 
                    !date.isBefore(booking.getCheckInDate()) && 
                    !date.isAfter(booking.getCheckOutDate())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    public int getTotalGuests() {
        try {
            return bookingDAO.findActiveBookings().size();
        } catch (Exception e) {
            System.err.println("Ошибка при получении количества гостей: " + e.getMessage());
            return 0;
        }
    }
    
    public double getPaymentAmountForRoom(String roomNumber) {
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                return 0.0;
            }
            
            Room room = roomOpt.get();
            
            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    long days = booking.getCheckOutDate().toEpochDay() - booking.getCheckInDate().toEpochDay();
                    double roomCost = room.getPrice() * days;
                    
                    List<BookingService> services = bookingServiceDAO.findByBookingId(booking.getId());
                    double servicesCost = services.stream()
                        .mapToDouble(bs -> bs.getService().getPrice())
                        .sum();
                    
                    return roomCost + servicesCost;
                }
            }
            
            return 0.0;
        } catch (Exception e) {
            System.err.println("Ошибка при расчете оплаты за номер: " + e.getMessage());
            return 0.0;
        }
    }
    
    public List<ServiceWithDate> getGuestServicesSortedByPrice(String guestName) {
        List<ServiceWithDate> allServices = new ArrayList<>();
        
        try {
            List<Guest> guests = guestDAO.findAll().stream()
                .filter(g -> g.getName().equals(guestName))
                .collect(Collectors.toList());
            
            for (Guest guest : guests) {
                List<Booking> bookings = bookingDAO.findByGuestId(guest.getId());
                for (Booking booking : bookings) {
                    List<BookingService> bookingServices = bookingServiceDAO.findByBookingId(booking.getId());
                    for (BookingService bs : bookingServices) {
                        allServices.add(new ServiceWithDate(bs.getService(), bs.getServiceDate()));
                    }
                }
            }
            
            Collections.sort(allServices, new Comparator<ServiceWithDate>() {
                @Override
                public int compare(ServiceWithDate swd1, ServiceWithDate swd2) {
                    return Double.compare(swd1.getService().getPrice(), swd2.getService().getPrice());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Ошибка при получении услуг гостя: " + e.getMessage());
        }
    
        return allServices;
    }
    
    public List<ServiceWithDate> getGuestServicesSortedByDate(String guestName) {
        List<ServiceWithDate> allServices = new ArrayList<>();
        
        try {
            List<Guest> guests = guestDAO.findAll().stream()
                .filter(g -> g.getName().equals(guestName))
                .collect(Collectors.toList());
            
            for (Guest guest : guests) {
                List<Booking> bookings = bookingDAO.findByGuestId(guest.getId());
                for (Booking booking : bookings) {
                    List<BookingService> bookingServices = bookingServiceDAO.findByBookingId(booking.getId());
                    for (BookingService bs : bookingServices) {
                        allServices.add(new ServiceWithDate(bs.getService(), bs.getServiceDate()));
                    }
                }
            }
            
            Collections.sort(allServices, new Comparator<ServiceWithDate>() {
                @Override
                public int compare(ServiceWithDate swd1, ServiceWithDate swd2) {
                    return swd1.getDate().compareTo(swd2.getDate());
                }
            });

        } catch (Exception e) {
            System.err.println("Ошибка при получении услуг гостя: " + e.getMessage());
        }

        return allServices;
    }
    
    public void printGuestServicesSortedByPrice(String guestName) {
        System.out.println("\n=== УСЛУГИ " + guestName + " (по цене) ===");
        List<ServiceWithDate> services = getGuestServicesSortedByPrice(guestName);
        for (ServiceWithDate serviceWithDate : services) {
            System.out.println(serviceWithDate);
        }
    }
    
    public void printGuestServicesSortedByDate(String guestName) {
        System.out.println("\n=== УСЛУГИ " + guestName + " (по дате) ===");
        List<ServiceWithDate> services = getGuestServicesSortedByDate(guestName);
        for (ServiceWithDate serviceWithDate : services) {
            System.out.println(serviceWithDate);
        }
    }
    
    public List<Booking> getLastThreeGuestsOfRoom(String roomNumber) {
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                return new ArrayList<>();
            }
            
            Room room = roomOpt.get();
            
            List<Booking> allBookings = bookingDAO.findByRoomId(room.getId());
            
            List<Booking> history = allBookings.stream()
                .filter(booking -> !booking.isActive())
                .sorted(Comparator.comparing(Booking::getCheckOutDate).reversed())
                .collect(Collectors.toList());
            
            int count = Math.min(3, history.size());
            return history.subList(0, count);
            
        } catch (Exception e) {
            System.err.println("Ошибка при получении истории номера: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void printRoomDetails(String roomNumber) {
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                System.out.println("Номер не найден");
                return;
            }
        
            Room room = roomOpt.get();
        
            System.out.println("\n=== ДЕТАЛИ НОМЕРА " + roomNumber + " ===");
            System.out.println("Тип: " + room.getType());
            System.out.println("Цена: " + room.getPrice() + " руб.");
            System.out.println("Вместимость: " + room.getCapacity() + " чел.");
            System.out.println("Звезды: " + room.getStars());
            System.out.println("Статус: " + room.getStatus());
        
            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            Booking activeBooking = null;
            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    activeBooking = booking;
                    break;
                }
            }
            
            if (activeBooking != null) {
                System.out.println("Текущий постоялец: " + activeBooking.getGuest().getName());
                System.out.println("Даты: " + activeBooking.getCheckInDate() + " - " + 
                                activeBooking.getCheckOutDate());
                
                List<BookingService> services = bookingServiceDAO.findByBookingId(activeBooking.getId());
                if (!services.isEmpty()) {
                    System.out.println("Услуги в бронировании:");
                    for (BookingService bs : services) {
                        System.out.println("  - " + bs.getService().getName() + 
                                         " (" + bs.getServiceDate() + ")");
                    }
                }
            }
        
            List<Booking> history = getLastThreeGuestsOfRoom(roomNumber);
            System.out.println("История бронирований: " + history.size() + " записей");
        
            System.out.println("Последние постояльцы (отображаются последние 3):");
            for (Booking booking : history) {
                System.out.println("  - " + booking.getGuest().getName() + 
                                " (" + booking.getCheckInDate() + " до " + booking.getCheckOutDate() + ")");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении деталей номера: " + e.getMessage());
        }
    }
    
    // Новый метод для получения всех услуг бронирования
    public void printBookingServices(Long bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                System.out.println("Бронирование не найдено");
                return;
            }
            
            Booking booking = bookingOpt.get();
            System.out.println("\n=== УСЛУГИ БРОНИРОВАНИЯ " + bookingId + " ===");
            System.out.println("Гость: " + booking.getGuest().getName());
            System.out.println("Номер: " + booking.getRoom().getNumber());
            
            List<BookingService> services = bookingServiceDAO.findByBookingId(bookingId);
            if (services.isEmpty()) {
                System.out.println("Услуги не найдены");
            } else {
                double total = 0;
                for (BookingService bs : services) {
                    System.out.println("  - " + bs.getService().getName() + 
                                     ", цена: " + bs.getService().getPrice() + " руб." +
                                     ", дата: " + bs.getServiceDate());
                    total += bs.getService().getPrice();
                }
                System.out.println("Общая стоимость услуг: " + total + " руб.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении услуг бронирования: " + e.getMessage());
        }
    }

    public void printPriceListSortedByCategoryAndPrice() {
        try {
            System.out.println("=== ПРАЙС-ЛИСТ ОТЕЛЯ ===");
            
            List<PricedItem> rooms = new ArrayList<>();
            List<PricedItem> services = new ArrayList<>();
            
            List<Room> allRooms = roomDAO.findAll();
            for (Room room : allRooms) {
                rooms.add(new PricedItem(
                    "Номер " + room.getNumber() + " (" + room.getType().getDisplayName() + ")",
                    room.getType().getDisplayName(),
                    room.getPrice()
                ));
            }
            
            List<Service> allServices = serviceDAO.findAll();
            for (Service service : allServices) {
                services.add(new PricedItem(
                    "Услуга: " + service.getName(),
                    service.getCategory().getDisplayName(),
                    service.getPrice()
                ));
            }
            
            Collections.sort(rooms, new Comparator<PricedItem>() {
                @Override
                public int compare(PricedItem item1, PricedItem item2) {
                    int categoryCompare = item1.category.compareTo(item2.category);
                    if (categoryCompare != 0) {
                        return categoryCompare;
                    }
                    return Double.compare(item1.price, item2.price);
                }
            });
            
            Collections.sort(services, new Comparator<PricedItem>() {
                @Override
                public int compare(PricedItem item1, PricedItem item2) {
                    int categoryCompare = item1.category.compareTo(item2.category);
                    if (categoryCompare != 0) {
                        return categoryCompare;
                    }
                    return Double.compare(item1.price, item2.price);
                }
            });
            
            System.out.println("\n--- НОМЕРА ---");
            String currentCategory = "";
            for (PricedItem item : rooms) {
                if (!item.category.equals(currentCategory)) {
                    currentCategory = item.category;
                    System.out.println("\n" + currentCategory + ":");
                }
                System.out.println("  " + item.name + " - " + item.price + " руб.");
            }
            
            System.out.println("\n--- УСЛУГИ ---");
            currentCategory = "";
            for (PricedItem item : services) {
                if (!item.category.equals(currentCategory)) {
                    currentCategory = item.category;
                    System.out.println("\n" + currentCategory + ":");
                }
                System.out.println("  " + item.name + " - " + item.price + " руб.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при выводе прайс-листа: " + e.getMessage());
        }
    }
    
    public void printAllRoomsSortedByPrice() {
        System.out.println("\n=== ВСЕ НОМЕРА (по цене) ===");
        List<Room> rooms = getRoomsSortedByPrice();
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Тип: %s, Цена: %.2f руб., Вместимость: %d, Звезды: %d, Статус: %s%n",
                room.getNumber(), room.getType(), room.getPrice(), 
                room.getCapacity(), room.getStars(), room.getStatus());
        }
    }
    
    public void printAllRoomsSortedByCapacity() {
        System.out.println("\n=== ВСЕ НОМЕРА (по вместимости) ===");
        List<Room> rooms = getRoomsSortedByCapacity();
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Вместимость: %d, Тип: %s, Цена: %.2f руб., Звезды: %d%n",
                room.getNumber(), room.getCapacity(), room.getType(), 
                room.getPrice(), room.getStars());
        }
    }
    
    public void printAllRoomsSortedByStars() {
        System.out.println("\n=== ВСЕ НОМЕРА (по звездам) ===");
        List<Room> rooms = getRoomsSortedByStars();
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Звезды: %d, Тип: %s, Цена: %.2f руб., Вместимость: %d%n",
                room.getNumber(), room.getStars(), room.getType(), 
                room.getPrice(), room.getCapacity());
        }
    }
    
    public void printAvailableRoomsSortedByPrice() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по цене) ===");
        List<Room> rooms = getAvailableRoomsSortedByPrice();
        if (rooms.isEmpty()) {
            System.out.println("Нет свободных номеров");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Цена: %.2f руб., Тип: %s, Вместимость: %d, Звезды: %d%n",
                room.getNumber(), room.getPrice(), room.getType(), 
                room.getCapacity(), room.getStars());
        }
    }
    
    public void printAvailableRoomsSortedByCapacity() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по вместимости) ===");
        List<Room> rooms = getAvailableRoomsSortedByCapacity();
        if (rooms.isEmpty()) {
            System.out.println("Нет свободных номеров");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Вместимость: %d, Цена: %.2f руб., Тип: %s, Звезды: %d%n",
                room.getNumber(), room.getCapacity(), room.getPrice(), 
                room.getType(), room.getStars());
        }
    }
    
    public void printAvailableRoomsSortedByStars() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по звездам) ===");
        List<Room> rooms = getAvailableRoomsSortedByStars();
        if (rooms.isEmpty()) {
            System.out.println("Нет свободных номеров");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Звезды: %d, Цена: %.2f руб., Тип: %s, Вместимость: %d%n",
                room.getNumber(), room.getStars(), room.getPrice(), 
                room.getType(), room.getCapacity());
        }
    }
    
    public void printGuestsSortedByName() {
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (по имени) ===");
        List<Booking> bookings = getGuestsAndRoomsSortedByName();
        if (bookings.isEmpty()) {
            System.out.println("Нет активных бронирований");
            return;
        }
        for (Booking booking : bookings) {
            System.out.printf("Гость: %s, Паспорт: %s, Комната: %s, Заезд: %s, Выезд: %s%n",
                booking.getGuest().getName(), booking.getGuest().getPassportNumber(),
                booking.getRoom().getNumber(), booking.getCheckInDate(), booking.getCheckOutDate());
        }
    }
    
    public void printGuestsSortedByCheckoutDate() {
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (по дате выезда) ===");
        List<Booking> bookings = getGuestsAndRoomsSortedByCheckoutDate();
        if (bookings.isEmpty()) {
            System.out.println("Нет активных бронирований");
            return;
        }
        for (Booking booking : bookings) {
            System.out.printf("Выезд: %s, Гость: %s, Комната: %s, Заезд: %s%n",
                booking.getCheckOutDate(), booking.getGuest().getName(),
                booking.getRoom().getNumber(), booking.getCheckInDate());
        }
    }
    
    public void printRoomsAvailableByDate(LocalDate date) {
        System.out.println("\n=== НОМЕРА, СВОБОДНЫЕ НА ДАТУ " + date + " ===");
        List<Room> rooms = getRoomsAvailableByDate(date);
        if (rooms.isEmpty()) {
            System.out.println("Нет свободных номеров на эту дату");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Тип: %s, Цена: %.2f руб., Вместимость: %d, Звезды: %d%n",
                room.getNumber(), room.getType(), room.getPrice(), 
                room.getCapacity(), room.getStars());
        }
    }
}