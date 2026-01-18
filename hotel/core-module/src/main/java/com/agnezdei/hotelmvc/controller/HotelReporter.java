package com.agnezdei.hotelmvc.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.impl.BookingRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestServiceRepository;
import com.agnezdei.hotelmvc.repository.impl.RoomRepository;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;

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
    private GuestServiceRepository guestServiceDAO;

    public HotelReporter() {
    }

    public List<Room> getRoomsSortedByPrice() {
        try {
            return roomDAO.findAllOrderedByPrice();
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат, отсортированных по цене: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByCapacity() {
        try {
            return roomDAO.findAllOrderedByCapacity();
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат, отсортированных по вместимости: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByStars() {
        try {
            return roomDAO.findAllOrderedByStars();
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат, отсортированных по звездам: " + e.getMessage());
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
        try {
            return roomDAO.findAvailableRoomsOrderedByPrice();
        } catch (Exception e) {
            System.err.println("Ошибка при получении доступных комнат, отсортированных по цене: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByCapacity() {
        try {
            return roomDAO.findAvailableRoomsOrderedByCapacity();
        } catch (Exception e) {
            System.err.println("Ошибка при получении доступных комнат, отсортированных по вместимости: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByStars() {
        try {
            return roomDAO.findAvailableRoomsOrderedByStars();
        } catch (Exception e) {
            System.err.println("Ошибка при получении доступных комнат, отсортированных по звездам: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int getTotalAvailableRooms() {
        try {
            return roomDAO.countAvailableRooms();
        } catch (Exception e) {
            System.err.println("Ошибка при получении количества свободных комнат: " + e.getMessage());
            return 0;
        }
    }

    public List<Booking> getGuestsAndRoomsSortedByName() {
        try {
            return bookingDAO.findActiveBookingsOrderedByGuestName();
        } catch (Exception e) {
            System.err.println("Ошибка при получении бронирований, отсортированных по имени гостя: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        try {
            return bookingDAO.findActiveBookingsOrderedByCheckoutDate();
        } catch (Exception e) {
            System.err.println("Ошибка при получении бронирований, отсортированных по дате выезда: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        try {
            return roomDAO.findRoomsAvailableOnDate(date);
        } catch (Exception e) {
            System.err.println("Ошибка при получении комнат на дату: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public int getTotalGuests() {
        try {
            return guestDAO.countGuestsWithActiveBookings();
        } catch (Exception e) {
            System.err.println("Ошибка при получении количества гостей: " + e.getMessage());
            return 0;
        }
    }
    
    public void getPaymentForRoom(String roomNumber) {
        try {
            Room room = roomDAO.findByNumber(roomNumber)
                .orElseThrow(() -> new Exception("Комната " + roomNumber + " не найдена"));
            
            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            Booking activeBooking = null;
            
            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    activeBooking = booking;
                    break;
                }
            }
            
            if (activeBooking == null) {
                System.out.println("В комнате " + roomNumber + " нет активного бронирования");
                return;
            }
            
            Guest guest = activeBooking.getGuest();
            
            long days = activeBooking.getCheckOutDate().toEpochDay() - 
                       activeBooking.getCheckInDate().toEpochDay();
            double roomCost = room.getPrice() * days;
            
            List<GuestService> guestServices = guestServiceDAO.findByGuestId(guest.getId());
            double serviceCost = 0.0;
            List<GuestService> periodServices = new ArrayList<>();
            
            for (GuestService gs : guestServices) {
                LocalDate serviceDate = gs.getServiceDate();
                if (!serviceDate.isBefore(activeBooking.getCheckInDate()) && 
                    !serviceDate.isAfter(activeBooking.getCheckOutDate())) {
                    serviceCost += gs.getService().getPrice();
                    periodServices.add(gs);
                }
            }
            
            System.out.println("\n=== СЧЕТ ДЛЯ ОПЛАТЫ ===");
            System.out.println("Комната: " + roomNumber);
            System.out.println("Гость: " + guest.getName() + " (паспорт: " + guest.getPassportNumber() + ")");
            System.out.println("Период проживания: " + activeBooking.getCheckInDate() + " - " + 
                              activeBooking.getCheckOutDate() + " (" + days + " дней)");
            System.out.println("\n--- Стоимость номера ---");
            System.out.println(room.getPrice() + " руб./день × " + days + " дней = " + roomCost + " руб.");
            
            if (!periodServices.isEmpty()) {
                System.out.println("\n--- Услуги за период ---");
                for (GuestService gs : periodServices) {
                    System.out.println(gs.getService().getName() + " (" + gs.getServiceDate() + "): " + 
                                     gs.getService().getPrice() + " руб.");
                }
                System.out.println("Итого за услуги: " + serviceCost + " руб.");
            }
            
            double total = roomCost + serviceCost;
            System.out.println("\n=== ОБЩАЯ СУММА К ОПЛАТЕ: " + total + " руб. ===");
            
        } catch (Exception e) {
            System.out.println("Ошибка при расчете оплаты: " + e.getMessage());
        }
    }
    
    public List<GuestService> getGuestServicesSortedByPrice(String guestName) {
        try {
            return guestServiceDAO.findByGuestNameOrderedByPrice(guestName);
        } catch (Exception e) {
            System.err.println("Ошибка при получении услуг гостя, отсортированных по цене: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<GuestService> getGuestServicesSortedByDate(String guestName) {
        try {
            return guestServiceDAO.findByGuestNameOrderedByDate(guestName);
        } catch (Exception e) {
            System.err.println("Ошибка при получении услуг гостя, отсортированных по дате: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void printGuestServicesSortedByPrice(String guestName) {
        System.out.println("\n=== УСЛУГИ ГОСТЯ " + guestName + " (по цене) ===");
        List<GuestService> services = getGuestServicesSortedByPrice(guestName);
        if (services.isEmpty()) {
            System.out.println("У гостя " + guestName + " нет заказанных услуг");
            return;
        }
        for (GuestService gs : services) {
            System.out.println("  - " + gs.getService().getName() + 
                             ": " + gs.getService().getPrice() + " руб." +
                             " (дата: " + gs.getServiceDate() + ")");
        }
    }
    
    public void printGuestServicesSortedByDate(String guestName) {
        System.out.println("\n=== УСЛУГИ ГОСТЯ " + guestName + " (по дате) ===");
        List<GuestService> services = getGuestServicesSortedByDate(guestName);
        if (services.isEmpty()) {
            System.out.println("У гостя " + guestName + " нет заказанных услуг");
            return;
        }
        for (GuestService gs : services) {
            System.out.println("  - " + gs.getService().getName() + 
                             ": " + gs.getService().getPrice() + " руб." +
                             " (дата: " + gs.getServiceDate() + ")");
        }
    }
    
    public List<Booking> getLastThreeGuestsOfRoom(String roomNumber) {
        try {
            Room room = roomDAO.findByNumber(roomNumber)
                .orElseThrow(() -> new Exception("Комната " + roomNumber + " не найдена"));
            
            return bookingDAO.findLastThreeGuestsByRoomId(room.getId());
            
        } catch (Exception e) {
            System.err.println("Ошибка при получении истории номера: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void printRoomDetails(String roomNumber) {
        try {
            Room room = roomDAO.findByNumber(roomNumber)
                .orElseThrow(() -> new Exception("Комната " + roomNumber + " не найдена"));
        
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
                
                List<GuestService> guestServices = guestServiceDAO.findByGuestId(activeBooking.getGuest().getId());
                if (!guestServices.isEmpty()) {
                    System.out.println("Услуги гостя:");
                    for (GuestService gs : guestServices) {
                        System.out.println("  - " + gs.getService().getName() + 
                                         " (" + gs.getServiceDate() + "): " + 
                                         gs.getService().getPrice() + " руб.");
                    }
                }
            } else {
                System.out.println("Текущий постоялец: нет");
            }
        
            List<Booking> history = getLastThreeGuestsOfRoom(roomNumber);
            System.out.println("История бронирований: " + history.size() + " записей");
        
            if (!history.isEmpty()) {
                System.out.println("Последние постояльцы:");
                for (Booking booking : history) {
                    System.out.println("  - " + booking.getGuest().getName() + 
                                    " (" + booking.getCheckInDate() + " до " + booking.getCheckOutDate() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении деталей номера: " + e.getMessage());
        }
    }

    public void printPriceListSortedByCategoryAndPrice() {
        try {
            System.out.println("=== ПРАЙС-ЛИСТ ОТЕЛЯ ===");
            
            List<Room> rooms = roomDAO.findAllOrderedByTypeAndPrice();
            List<Service> services = serviceDAO.findAllOrderedByCategoryAndPrice();
            
            System.out.println("\n--- НОМЕРА ---");
            RoomType currentType = null;
            for (Room room : rooms) {
                if (currentType != room.getType()) {
                    currentType = room.getType();
                    System.out.println("\n" + currentType.getDisplayName() + ":");
                }
                System.out.println("  Номер " + room.getNumber() + " - " + room.getPrice() + " руб.");
            }
            
            System.out.println("\n--- УСЛУГИ ---");
            ServiceCategory currentCategory = null;
            for (Service service : services) {
                if (currentCategory != service.getCategory()) {
                    currentCategory = service.getCategory();
                    System.out.println("\n" + currentCategory.getDisplayName() + ":");
                }
                System.out.println("  " + service.getName() + " - " + service.getPrice() + " руб.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при выводе прайс-листа: " + e.getMessage());
        }
    }
    
    public void printAllRoomsSortedByPrice() {
        System.out.println("\n=== ВСЕ НОМЕРА (по цене) ===");
        List<Room> rooms = getRoomsSortedByPrice();
        if (rooms.isEmpty()) {
            System.out.println("Нет номеров в базе данных");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Тип: %s, Цена: %.2f руб., Вместимость: %d, Звезды: %d, Статус: %s%n",
                room.getNumber(), room.getType(), room.getPrice(), 
                room.getCapacity(), room.getStars(), room.getStatus());
        }
    }
    
    public void printAllRoomsSortedByCapacity() {
        System.out.println("\n=== ВСЕ НОМЕРА (по вместимости) ===");
        List<Room> rooms = getRoomsSortedByCapacity();
        if (rooms.isEmpty()) {
            System.out.println("Нет номеров в базе данных");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Вместимость: %d, Тип: %s, Цена: %.2f руб., Звезды: %d%n",
                room.getNumber(), room.getCapacity(), room.getType(), 
                room.getPrice(), room.getStars());
        }
    }
    
    public void printAllRoomsSortedByStars() {
        System.out.println("\n=== ВСЕ НОМЕРА (по звездам) ===");
        List<Room> rooms = getRoomsSortedByStars();
        if (rooms.isEmpty()) {
            System.out.println("Нет номеров в базе данных");
            return;
        }
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