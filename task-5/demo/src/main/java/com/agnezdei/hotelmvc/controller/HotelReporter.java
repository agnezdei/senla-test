package com.agnezdei.hotelmvc.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.agnezdei.hotelmvc.model.*;

import java.util.Comparator;
import java.util.Collections;

public class HotelReporter {
    private Hotel hotel;

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

    public HotelReporter(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<Room> getRoomsSortedByPrice() {
        return hotel.getRooms().stream()
            .sorted(Comparator.comparing(Room::getPrice))
            .collect(Collectors.toList());
    }

    public List<Room> getRoomsSortedByCapacity() {
        return hotel.getRooms().stream()
            .sorted(Comparator.comparing(Room::getCapacity))
            .collect(Collectors.toList());
    }

    public List<Room> getRoomsSortedByStars() {
        return hotel.getRooms().stream()
            .sorted(Comparator.comparing(Room::getStars))
            .collect(Collectors.toList());
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
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
        return (int) hotel.getRooms().stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
            .count();
    }

    private List<Booking> getAllActiveBookings() {
        return hotel.getRooms().stream()
            .map(Room::getCurrentBooking)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<Booking> getGuestsAndRoomsSortedByName() {
        return getAllActiveBookings().stream()
            .sorted(Comparator.comparing(booking -> booking.getGuest().getName()))
            .collect(Collectors.toList());
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        return getAllActiveBookings().stream()
            .sorted(Comparator.comparing(Booking::getCheckOutDate))
            .collect(Collectors.toList());
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        return hotel.getRooms().stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE || 
                           (room.getCurrentBooking() != null && 
                            room.getCurrentBooking().getCheckOutDate().isBefore(date)))
            .collect(Collectors.toList());
    }

    public int getTotalGuests() {
        return (int) hotel.getRooms().stream()
            .map(Room::getCurrentBooking)
            .filter(Objects::nonNull)
            .count();
    }

    public double getPaymentAmountForRoom(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room != null && room.getCurrentBooking() != null) {
            return room.getCurrentBooking().calculateTotalPrice();
        }
        return 0.0;
    }

    public List<Booking.ServiceWithDate> getGuestServicesSortedByPrice(String guestName) {
        List<Booking.ServiceWithDate> allServices = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            Booking booking = room.getCurrentBooking();
            if (booking != null && booking.getGuest().getName().equals(guestName)) {
                allServices.addAll(booking.getServices());
            }
        }
        
        Collections.sort(allServices, new Comparator<Booking.ServiceWithDate>() {
        @Override
        public int compare(Booking.ServiceWithDate swd1, Booking.ServiceWithDate swd2) {
            return Double.compare(swd1.getService().getPrice(), swd2.getService().getPrice());
        }
    });
    
        return allServices;
    }


    public List<Booking.ServiceWithDate> getGuestServicesSortedByDate(String guestName) {
        List<Booking.ServiceWithDate> allServices = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            Booking booking = room.getCurrentBooking();
            if (booking != null && booking.getGuest().getName().equals(guestName)) {
                allServices.addAll(booking.getServices());
            }
        }
        
        Collections.sort(allServices, new Comparator<Booking.ServiceWithDate>() {
        @Override
        public int compare(Booking.ServiceWithDate swd1, Booking.ServiceWithDate swd2) {
            return swd1.getDate().compareTo(swd2.getDate());
        }
        });

        return allServices;
    }

    public void printGuestServicesSortedByPrice(String guestName) {
        System.out.println("\n=== УСЛУГИ " + guestName + " (по цене) ===");
        List<Booking.ServiceWithDate> services = getGuestServicesSortedByPrice(guestName);
        for (Booking.ServiceWithDate serviceWithDate : services) {
            System.out.println(serviceWithDate.getService().getName() + 
                             " - " + serviceWithDate.getService().getPrice() + " руб." +
                             " (дата: " + serviceWithDate.getDate() + ")");
        }
    }

    public void printGuestServicesSortedByDate(String guestName) {
        System.out.println("\n=== УСЛУГИ " + guestName + " (по дате) ===");
        List<Booking.ServiceWithDate> services = getGuestServicesSortedByDate(guestName);
        for (Booking.ServiceWithDate serviceWithDate : services) {
            System.out.println(serviceWithDate.getService().getName() + 
                             " - " + serviceWithDate.getService().getPrice() + " руб." +
                             " (дата: " + serviceWithDate.getDate() + ")");
        }
    }

    public List<Booking> getLastThreeGuestsOfRoom(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return new ArrayList<>();
        }
    
        List<Booking> history = room.getBookingHistory();
        
        int startIndex = Math.max(0, history.size() - 3);
        return new ArrayList<>(history.subList(startIndex, history.size()));
    }

    public void printRoomDetails(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Номер не найден");
            return;
        }
    
        System.out.println("\n=== ДЕТАЛИ НОМЕРА " + roomNumber + " ===");
        System.out.println("Тип: " + room.getType());
        System.out.println("Цена: " + room.getPrice() + " руб.");
        System.out.println("Вместимость: " + room.getCapacity() + " чел.");
        System.out.println("Звезды: " + room.getStars());
        System.out.println("Статус: " + room.getStatus());
    
        if (room.getCurrentBooking() != null) {
            System.out.println("Текущий постоялец: " + room.getCurrentBooking().getGuest().getName());
            System.out.println("Даты: " + room.getCurrentBooking().getCheckInDate() + " - " + 
                            room.getCurrentBooking().getCheckOutDate());
        }
    
        List<Booking> history = room.getBookingHistory();
        System.out.println("История бронирований: " + history.size() + " записей");
    
        System.out.println("Последние постояльцы (отображаются последние 3):");
        List<Booking> lastThree = getLastThreeGuestsOfRoom(roomNumber);
        for (Booking booking : lastThree) {
            System.out.println("  - " + booking.getGuest().getName() + 
                            " (" + booking.getCheckInDate() + " до " + booking.getCheckOutDate() + ")");
        }
    }

    public void printPriceListSortedByCategoryAndPrice() {
    System.out.println("=== ПРАЙС-ЛИСТ ОТЕЛЯ ===");
    
    List<PricedItem> rooms = new ArrayList<>();
    List<PricedItem> services = new ArrayList<>();
    
    for (Room room : hotel.getRooms()) {
        rooms.add(new PricedItem(
            "Номер " + room.getNumber() + " (" + room.getType().getDisplayName() + ")",
            room.getType().getDisplayName(),
            room.getPrice()
        ));
    }
    
    for (Service service : hotel.getServices()) {
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
    }
}