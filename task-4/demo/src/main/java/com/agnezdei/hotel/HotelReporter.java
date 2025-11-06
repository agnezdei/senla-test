package com.agnezdei.hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Double.compare(room1.getPrice(), room2.getPrice());
            }
        });
        
        return rooms;
    }

    public List<Room> getRoomsSortedByCapacity() {
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Integer.compare(room1.getCapacity(), room2.getCapacity());
            }
        });

        return rooms;
    }

    public List<Room> getRoomsSortedByStars() {
        List<Room> rooms = new ArrayList<>(hotel.getRooms());
    
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Integer.compare(room1.getStars(), room2.getStars());
            }
        });

        return rooms;
        
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
        List<Room> availableRooms = getAvailableRooms();

        Collections.sort(availableRooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Double.compare(room1.getPrice(), room2.getPrice());
            }
        });

        return availableRooms;
    }

   public List<Room> getAvailableRoomsSortedByCapacity() {
        List<Room> availableRooms = getAvailableRooms();

        Collections.sort(availableRooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Integer.compare(room1.getCapacity(), room2.getCapacity());
            }
        });

        return availableRooms;
    }

   public List<Room> getAvailableRoomsSortedByStars() {
        List<Room> availableRooms = getAvailableRooms();

        Collections.sort(availableRooms, new Comparator<Room>() {
            @Override
            public int compare(Room room1, Room room2) {
                return Integer.compare(room1.getStars(), room2.getStars());
            }
        });

        return availableRooms;
    }

    public int getTotalAvailableRooms() {
        int count = 0;
        for (Room room : hotel.getRooms()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
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
        
        Collections.sort(activeBookings, new Comparator<Booking>() {
        @Override
        public int compare(Booking b1, Booking b2) {
            return b1.getGuest().getName().compareTo(b2.getGuest().getName());
        }
        });

        return activeBookings;
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        List<Booking> activeBookings = getAllActiveBookings();
        
        Collections.sort(activeBookings, new Comparator<Booking>() {
        @Override
        public int compare(Booking b1, Booking b2) {
            return b1.getCheckOutDate().compareTo(b2.getCheckOutDate());
        }
        });

        return activeBookings;
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : hotel.getRooms()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                availableRooms.add(room);
            } else if (room.getCurrentBooking() != null) {
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