import java.util.ArrayList;
import java.util.List;

class Guest {
    private String name;
    private String passportNumber;
    
    public Guest(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }
    
    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    
    @Override
    public String toString() {
        return name + " (паспорт: " + passportNumber + ")";
    }
}

class Service {
    private String name;
    private double price;
    private Hotel hotel;
    
    public Service(String name, double price, Hotel hotel) {
        this.name = name;
        this.price = price;
        this.hotel = hotel;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    @Override
    public String toString() {
        return name + " - " + price + " руб.";
    }
}

class Booking {
    private Guest guest;
    private Room room;
    
    public Booking(Guest guest, Room room) {
        this.guest = guest;
        this.room = room;
    }
    
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    
    @Override
    public String toString() {
        return "Бронирование: " + guest.getName() + " в номере " + room.getNumber();
    }
}

class Room {
    private String number;
    private String type;
    private double price;
    private String status;
    private Booking currentBooking;
    private Hotel hotel;
    
    public Room(String number, String type, double price, Hotel hotel) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = "available";
        this.hotel = hotel;
    }
    
    public String getNumber() { return number; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public Booking getCurrentBooking() { return currentBooking; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentBooking(Booking booking) { this.currentBooking = booking; }
    
    @Override
    public String toString() {
        return "Номер " + number + " (" + type + ") - " + price + " руб. [" + status + "]";
    }
}

class Hotel {
    private String name;
    private List<Room> rooms;
    private List<Service> services;
    
    public Hotel(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
        this.services = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }
    public List<Service> getServices() { return new ArrayList<>(services); }
    
    public void addRoom(Room room) {
        rooms.add(room);
    }
    
    public void addService(Service service) {
        services.add(service);
    }
    
    public Room findRoom(String number) {
        for (Room room : rooms) {
            if (room.getNumber().equals(number)) {
                return room;
            }
        }
        return null;
    }
    
    public Service findService(String name) {
        for (Service service : services) {
            if (service.getName().equals(name)) {
                return service;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Отель '" + name + "' (" + rooms.size() + " номеров, " + services.size() + " услуг)";
    }
}

class HotelAdmin {
    private Hotel hotel;
    
    public HotelAdmin(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public void settleGuest(String roomNumber, Guest guest) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (!"available".equals(room.getStatus())) {
            System.out.println("Ошибка: Номер " + roomNumber + " недоступен для заселения");
            return;
        }
        
        Booking booking = new Booking(guest, room);
        room.setCurrentBooking(booking);
        room.setStatus("occupied");
        System.out.println("Успех: " + guest.getName() + " заселен в номер " + roomNumber);
    }
    
    public void evictGuest(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (!"occupied".equals(room.getStatus())) {
            System.out.println("Ошибка: Номер " + roomNumber + " не занят");
            return;
        }
        
        String guestName = room.getCurrentBooking().getGuest().getName();
        room.setCurrentBooking(null);
        room.setStatus("available");
        System.out.println("Успех: " + guestName + " выселен из номера " + roomNumber);
    }
    
    public void setRoomUnderMaintenance(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if ("occupied".equals(room.getStatus())) {
            System.out.println("Ошибка: Нельзя перевести занятый номер на ремонт");
            return;
        }
        
        room.setStatus("under_maintenance");
        System.out.println("Успех: Номер " + roomNumber + " переведен на ремонт");
    }
    
    public void setRoomAvailable(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        room.setStatus("available");
        System.out.println("Успех: Номер " + roomNumber + " доступен для бронирования");
    }
    
    public void changeRoomPrice(String roomNumber, double newPrice) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        room.setPrice(newPrice);
        System.out.println("Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.");
    }
    
    public void changeServicePrice(String serviceName, double newPrice) {
        Service service = hotel.findService(serviceName);
        if (service == null) {
            System.out.println("Ошибка: Услуга '" + serviceName + "' не найдена");
            return;
        }
        
        service.setPrice(newPrice);
        System.out.println("Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.");
    }
    
    public void addRoom(String number, String type, double price) {
        if (hotel.findRoom(number) != null) {
            System.out.println("Ошибка: Номер " + number + " уже существует");
            return;
        }
        
        Room room = new Room(number, type, price, hotel);
        hotel.addRoom(room);
        System.out.println("Успех: Добавлен номер " + number);
    }
    
    public void addService(String name, double price) {
        if (hotel.findService(name) != null) {
            System.out.println("Ошибка: Услуга '" + name + "' уже существует");
            return;
        }
        
        Service service = new Service(name, price, hotel);
        hotel.addService(service);
        System.out.println("Успех: Добавлена услуга '" + name + "'");
    }
    
    public void showHotelInfo() {
        System.out.println("\n=== ИНФОРМАЦИЯ ОБ ОТЕЛЕ ===");
        System.out.println(hotel);
        
        System.out.println("\nНОМЕРА:");
        for (Room room : hotel.getRooms()) {
            System.out.println("  " + room);
            if (room.getCurrentBooking() != null) {
                System.out.println("    Занят: " + room.getCurrentBooking().getGuest());
            }
        }
        
        System.out.println("\nУСЛУГИ:");
        for (Service service : hotel.getServices()) {
            System.out.println("  " + service);
        }
    }
}

public class testHotel {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        Hotel hotel = new Hotel("Гранд Отель");
        HotelAdmin admin = new HotelAdmin(hotel);
        
        admin.addRoom("101", "Стандарт", 2500);
        admin.addRoom("102", "Люкс", 5000);
        admin.addRoom("201", "Бизнес", 3500);
        
        admin.addService("Завтрак", 500);
        admin.addService("Уборка", 300);
        admin.addService("СПА", 1500);
        
        Guest guest1 = new Guest("Иван Иванов", "1234 567890");
        Guest guest2 = new Guest("Петр Петров", "9876 543210");
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ ОПЕРАЦИЙ ===");
        
        admin.settleGuest("101", guest1);
        admin.settleGuest("102", guest2);
        
        admin.settleGuest("101", new Guest("Сергей Сергеев", "1111 222222"));
        
        admin.changeRoomPrice("201", 4000);
        admin.changeServicePrice("Завтрак", 600);
        
        admin.setRoomUnderMaintenance("201");
        
        admin.setRoomUnderMaintenance("101");
        
        admin.evictGuest("101");
        
        admin.setRoomUnderMaintenance("101");
        
        admin.setRoomAvailable("101");
        
        admin.showHotelInfo();
    }
}