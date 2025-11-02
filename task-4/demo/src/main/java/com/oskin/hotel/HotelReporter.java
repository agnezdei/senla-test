package com.oskin.hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        for (int i = 0; i < rooms.size() - 1; i++) {
            for (int j = 0; j <  rooms.size() - i - 1; j++) {
                if (rooms.get(j).getPrice() > rooms.get(j+1).getPrice()) {
                    Room temp = rooms.get(j);
                    rooms.set(j, rooms.get(j + 1));
                    rooms.set(j + 1, temp);
                }
            }
        }
        return rooms;
    }

    public List<Room> getRoomsSortedByCapacity() {
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        for (int i = 0; i < rooms.size() - 1; i++) {
            for (int j = 0; j <  rooms.size() - i - 1; j++) {
                if (rooms.get(j).getCapacity() > rooms.get(j+1).getCapacity()) {
                    Room temp = rooms.get(j);
                    rooms.set(j, rooms.get(j + 1));
                    rooms.set(j + 1, temp);
                }
            }
        }
        return rooms;
    }

    public List<Room> getRoomsSortedByStars() {
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        for (int i = 0; i < rooms.size() - 1; i++) {
            for (int j = 0; j <  rooms.size() - i - 1; j++) {
                if (rooms.get(j).getStars() > rooms.get(j+1).getStars()) {
                    Room temp = rooms.get(j);
                    rooms.set(j, rooms.get(j + 1));
                    rooms.set(j + 1, temp);
                }
            }
        }
        return rooms;
        
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            if ("available".equals(room.getStatus())) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public List<Room> getAvailableRoomsSortedByPrice() {
        List<Room> availableRooms = getAvailableRooms();

        for (int i = 0; i < availableRooms.size() - 1; i++) {
            for (int j = 0; j < availableRooms.size() - i - 1; j++){
                if (availableRooms.get(j).getPrice() >
                availableRooms.get(j + 1).getPrice()) {
                    Room temp = availableRooms.get(j);
                    availableRooms.set(j, availableRooms.get(j + 1));
                    availableRooms.set(j + 1, temp);
                }
            }
        }
        return availableRooms;
    }

   public List<Room> getAvailableRoomsSortedByCapacity() {
        List<Room> availableRooms = getAvailableRooms();

        for (int i = 0; i < availableRooms.size() - 1; i++) {
            for (int j = 0; j < availableRooms.size() - i - 1; j++){
                if (availableRooms.get(j).getCapacity() >
                availableRooms.get(j + 1).getCapacity()) {
                    Room temp = availableRooms.get(j);
                    availableRooms.set(j, availableRooms.get(j + 1));
                    availableRooms.set(j + 1, temp);
                }
            }
        }
        return availableRooms;
    }

   public List<Room> getAvailableRoomsSortedByStars() {
        List<Room> availableRooms = getAvailableRooms();

        for (int i = 0; i < availableRooms.size() - 1; i++) {
            for (int j = 0; j < availableRooms.size() - i - 1; j++){
                if (availableRooms.get(j).getStars() >
                availableRooms.get(j + 1).getStars()) {
                    Room temp = availableRooms.get(j);
                    availableRooms.set(j, availableRooms.get(j + 1));
                    availableRooms.set(j + 1, temp);
                }
            }
        }
        return availableRooms;
    }

    public int getTotalAvailableRooms() {
        int count = 0;
        for (Room room : hotel.getRooms()) {
            if ("available".equals(room.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private List<Booking> getAllActiveBookings() {
        List<Booking> activeBookings = new ArrayList<>();
        for (Room room : hotel.getRooms()) {
            if (room.getCurrentBooking() != null) {
                activeBookings.add(room.getCurrentBooking());
            }
        }
        return activeBookings;
    }

    public List<Booking> getGuestsAndRoomsSortedByName() {
        List<Booking> activeBookings = getAllActiveBookings();
        
        for (int i = 0; i < activeBookings.size() - 1; i++) {
            for (int j = 0; j < activeBookings.size() - i - 1; j++) {
                String name1 = activeBookings.get(j).getGuest().getName();
                String name2 = activeBookings.get(j + 1).getGuest().getName();
                if (name1.compareTo(name2) > 0) {
                    Booking temp = activeBookings.get(j);
                    activeBookings.set(j, activeBookings.get(j + 1));
                    activeBookings.set(j + 1, temp);
                }
            }
        }
        return activeBookings;
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        List<Booking> activeBookings = getAllActiveBookings();
        
        for (int i = 0; i < activeBookings.size() - 1; i++) {
            for (int j = 0; j < activeBookings.size() - i - 1; j++) {
                LocalDate date1 = activeBookings.get(j).getCheckOutDate();
                LocalDate date2 = activeBookings.get(j + 1).getCheckOutDate();
                
                if (date1.compareTo(date2) > 0) {
                    Booking temp = activeBookings.get(j);
                    activeBookings.set(j, activeBookings.get(j + 1));
                    activeBookings.set(j + 1, temp);
                }
            }
        }
        return activeBookings;
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            if ("available".equals(room.getStatus())) {
                availableRooms.add(room);
            } else if (room.getCurrentBooking() != null) {
                // Если номер занят, но освободится до указанной даты
                if (room.getCurrentBooking().getCheckOutDate().isBefore(date)) {
                    availableRooms.add(room);
                }
            }
        }
        return availableRooms;
    }

    public int getTotalGuests() {
        return getAllActiveBookings().size();
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
        
        for (int i = 0; i < allServices.size() - 1; i++) {
            for (int j = 0; j < allServices.size() - i - 1; j++) {
                double price1 = allServices.get(j).getService().getPrice();
                double price2 = allServices.get(j + 1).getService().getPrice();
                if (price1 > price2) {
                    Booking.ServiceWithDate temp = allServices.get(j);
                    allServices.set(j, allServices.get(j + 1));
                    allServices.set(j + 1, temp);
                }
            }
        }
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
        
        for (int i = 0; i < allServices.size() - 1; i++) {
            for (int j = 0; j < allServices.size() - i - 1; j++) {
                LocalDate date1 = allServices.get(j).getDate();
                LocalDate date2 = allServices.get(j + 1).getDate();
                if (date1.compareTo(date2) > 0) {
                    Booking.ServiceWithDate temp = allServices.get(j);
                    allServices.set(j, allServices.get(j + 1));
                    allServices.set(j + 1, temp);
                }
            }
        }
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
        List<Booking> lastThree = new ArrayList<>();
        
        List<Booking> nonNullHistory = new ArrayList<>();
        for (Booking booking : history) {
            if (booking != null) {
            nonNullHistory.add(booking);
        }
        }
    
        int startIndex = Math.max(0, nonNullHistory.size() - 3);
        for (int i = startIndex; i < nonNullHistory.size(); i++) {
            lastThree.add(nonNullHistory.get(i));
        }
        
        return lastThree;
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
            "Номер " + room.getNumber() + " (" + room.getType() + ")",
            room.getType(),
            room.getPrice()
        ));
    }
    
    for (Service service : hotel.getServices()) {
        services.add(new PricedItem(
            "Услуга: " + service.getName(),
            service.getCategory(),
            service.getPrice()
        ));
    }
    
    for (int i = 0; i < rooms.size() - 1; i++) {
        for (int j = 0; j < rooms.size() - i - 1; j++) {
            PricedItem item1 = rooms.get(j);
            PricedItem item2 = rooms.get(j + 1);
            
            if (item1.category.compareTo(item2.category) > 0 || 
                (item1.category.equals(item2.category) && item1.price > item2.price)) {
                PricedItem temp = rooms.get(j);
                rooms.set(j, rooms.get(j + 1));
                rooms.set(j + 1, temp);
            }
        }
    }
    
    for (int i = 0; i < services.size() - 1; i++) {
        for (int j = 0; j < services.size() - i - 1; j++) {
            PricedItem item1 = services.get(j);
            PricedItem item2 = services.get(j + 1);
            
            if (item1.category.compareTo(item2.category) > 0 || 
                (item1.category.equals(item2.category) && item1.price > item2.price)) {
                PricedItem temp = services.get(j);
                services.set(j, services.get(j + 1));
                services.set(j + 1, temp);
            }
        }
    }
    
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