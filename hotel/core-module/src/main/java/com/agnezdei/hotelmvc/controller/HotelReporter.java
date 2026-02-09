package com.agnezdei.hotelmvc.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import com.agnezdei.hotelmvc.repository.ServiceDAO;

public class HotelReporter {
    @Inject
    private RoomDAO roomDAO;

    @Inject
    private GuestDAO guestDAO;

    @Inject
    private ServiceDAO serviceDAO;

    @Inject
    private BookingDAO bookingDAO;

    @Inject
    private GuestServiceDAO guestServiceDAO;

    private static final Logger logger = LoggerFactory.getLogger(HotelReporter.class);

    public HotelReporter() {
    }

    public List<Room> getRoomsSortedByPrice() {
        logger.debug("Начало получения номеров отсортированных по цене");
        try {
            List<Room> rooms = roomDAO.findAllOrderedByPrice();
            logger.debug("Получено {} номеров, отсортированных по цене", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении комнат, отсортированных по цене: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByCapacity() {
        logger.debug("Начало получения номеров отсортированных по вместимости");
        try {
            List<Room> rooms = roomDAO.findAllOrderedByCapacity();
            logger.debug("Получено {} номеров, отсортированных по вместимости", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении комнат, отсортированных по вместимости: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsSortedByStars() {
        logger.debug("Начало получения номеров отсортированных по звездам");
        try {
            List<Room> rooms = roomDAO.findAllOrderedByStars();
            logger.debug("Получено {} номеров, отсортированных по звездам", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении комнат, отсортированных по звездам: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRooms() {
        logger.debug("Начало получения доступных номеров");
        try {
            List<Room> rooms = roomDAO.findAvailableRooms();
            logger.debug("Получено {} доступных номеров", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении доступных комнат: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByPrice() {
        logger.debug("Начало получения доступных номеров отсортированных по цене");
        try {
            List<Room> rooms = roomDAO.findAvailableRoomsOrderedByPrice();
            logger.debug("Получено {} доступных номеров, отсортированных по цене", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении доступных комнат, отсортированных по цене: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByCapacity() {
        logger.debug("Начало получения доступных номеров отсортированных по вместимости");
        try {
            List<Room> rooms = roomDAO.findAvailableRoomsOrderedByCapacity();
            logger.debug("Получено {} доступных номеров, отсортированных по вместимости", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении доступных комнат, отсортированных по вместимости: {}", e.getMessage(),
                    e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAvailableRoomsSortedByStars() {
        logger.debug("Начало получения доступных номеров отсортированных по звездам");
        try {
            List<Room> rooms = roomDAO.findAvailableRoomsOrderedByStars();
            logger.debug("Получено {} доступных номеров, отсортированных по звездам", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении доступных комнат, отсортированных по звездам: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public int getTotalAvailableRooms() {
        logger.debug("Начало получения количества доступных номеров");
        try {
            int count = roomDAO.countAvailableRooms();
            logger.debug("Количество доступных номеров: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Ошибка при получении количества свободных комнат: {}", e.getMessage(), e);
            return 0;
        }
    }

    public List<Booking> getGuestsAndRoomsSortedByName() {
        logger.debug("Начало получения бронирований отсортированных по имени гостя");
        try {
            List<Booking> bookings = bookingDAO.findActiveBookingsOrderedByGuestName();
            logger.debug("Получено {} активных бронирований, отсортированных по имени гостя", bookings.size());
            return bookings;
        } catch (Exception e) {
            logger.error("Ошибка при получении бронирований, отсортированных по имени гостя: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Booking> getGuestsAndRoomsSortedByCheckoutDate() {
        logger.debug("Начало получения бронирований отсортированных по дате выезда");
        try {
            List<Booking> bookings = bookingDAO.findActiveBookingsOrderedByCheckoutDate();
            logger.debug("Получено {} активных бронирований, отсортированных по дате выезда", bookings.size());
            return bookings;
        } catch (Exception e) {
            logger.error("Ошибка при получении бронирований, отсортированных по дате выезда: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        logger.debug("Начало получения номеров доступных на дату: {}", date);
        try {
            List<Room> rooms = roomDAO.findRoomsAvailableOnDate(date);
            logger.debug("Получено {} номеров доступных на дату {}", rooms.size(), date);
            return rooms;
        } catch (Exception e) {
            logger.error("Ошибка при получении комнат на дату {}: {}", date, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public int getTotalGuests() {
        logger.debug("Начало получения общего количества гостей");
        try {
            int count = guestDAO.countGuestsWithActiveBookings();
            logger.debug("Общее количество гостей: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Ошибка при получении количества гостей: {}", e.getMessage(), e);
            return 0;
        }
    }

    public void getPaymentForRoom(String roomNumber) {
        logger.info("Начало расчета оплаты для номера: {}", roomNumber);
        try {
            Room room = roomDAO.findByNumber(roomNumber)
                    .orElseThrow(() -> new Exception("Комната " + roomNumber + " не найдена"));
            logger.debug("Найден номер: {}", roomNumber);

            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            Booking activeBooking = null;

            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    activeBooking = booking;
                    break;
                }
            }

            if (activeBooking == null) {
                String message = "В комнате " + roomNumber + " нет активного бронирования";
                logger.warn(message);
                System.out.println(message);
                return;
            }

            Guest guest = activeBooking.getGuest();
            logger.debug("Найден активный гость: {} (ID: {})", guest.getName(), guest.getId());

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

            double total = roomCost + serviceCost;

            // Вывод в консоль (оставляем как было)
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

            System.out.println("\n=== ОБЩАЯ СУММА К ОПЛАТЕ: " + total + " руб. ===");

            logger.info(
                    "Счет для номера {} сгенерирован: {} дней, стоимость номера: {} руб., услуги: {} руб., всего: {} руб.",
                    roomNumber, days, roomCost, serviceCost, total);

        } catch (Exception e) {
            String errorMsg = "Ошибка при расчете оплаты для номера " + roomNumber + ": " + e.getMessage();
            logger.error(errorMsg, e);
            System.out.println("Ошибка при расчете оплаты: " + e.getMessage());
        }
    }

    public List<GuestService> getGuestServicesSortedByPrice(String guestName) {
        logger.debug("Начало получения услуг гостя {}, отсортированных по цене", guestName);
        try {
            List<GuestService> services = guestServiceDAO.findByGuestNameOrderedByPrice(guestName);
            logger.debug("Получено {} услуг гостя {}, отсортированных по цене", services.size(), guestName);
            return services;
        } catch (Exception e) {
            logger.error("Ошибка при получении услуг гостя {}, отсортированных по цене: {}",
                    guestName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<GuestService> getGuestServicesSortedByDate(String guestName) {
        logger.debug("Начало получения услуг гостя {}, отсортированных по дате", guestName);
        try {
            List<GuestService> services = guestServiceDAO.findByGuestNameOrderedByDate(guestName);
            logger.debug("Получено {} услуг гостя {}, отсортированных по дате", services.size(), guestName);
            return services;
        } catch (Exception e) {
            logger.error("Ошибка при получении услуг гостя {}, отсортированных по дате: {}",
                    guestName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void printGuestServicesSortedByPrice(String guestName) {
        logger.info("Начало вывода услуг гостя {} отсортированных по цене", guestName);
        System.out.println("\n=== УСЛУГИ ГОСТЯ " + guestName + " (по цене) ===");
        List<GuestService> services = getGuestServicesSortedByPrice(guestName);
        if (services.isEmpty()) {
            String message = "У гостя " + guestName + " нет заказанных услуг";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (GuestService gs : services) {
            System.out.println("  - " + gs.getService().getName() +
                    ": " + gs.getService().getPrice() + " руб." +
                    " (дата: " + gs.getServiceDate() + ")");
        }
        logger.info("Выведено {} услуг гостя {} отсортированных по цене", services.size(), guestName);
    }

    public void printGuestServicesSortedByDate(String guestName) {
        logger.info("Начало вывода услуг гостя {} отсортированных по дате", guestName);
        System.out.println("\n=== УСЛУГИ ГОСТЯ " + guestName + " (по дате) ===");
        List<GuestService> services = getGuestServicesSortedByDate(guestName);
        if (services.isEmpty()) {
            String message = "У гостя " + guestName + " нет заказанных услуг";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (GuestService gs : services) {
            System.out.println("  - " + gs.getService().getName() +
                    ": " + gs.getService().getPrice() + " руб." +
                    " (дата: " + gs.getServiceDate() + ")");
        }
        logger.info("Выведено {} услуг гостя {} отсортированных по дате", services.size(), guestName);
    }

    public List<Booking> getLastThreeGuestsOfRoom(String roomNumber) {
        logger.debug("Начало получения истории номера: {}", roomNumber);
        try {
            Room room = roomDAO.findByNumber(roomNumber)
                    .orElseThrow(() -> new Exception("Комната " + roomNumber + " не найдена"));

            List<Booking> history = bookingDAO.findLastThreeGuestsByRoomId(room.getId());
            logger.debug("Получено {} записей истории для номера {}", history.size(), roomNumber);
            return history;

        } catch (Exception e) {
            logger.error("Ошибка при получении истории номера {}: {}", roomNumber, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void printRoomDetails(String roomNumber) {
        logger.info("Начало вывода деталей номера: {}", roomNumber);
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

            logger.info("Детали номера {} выведены успешно", roomNumber);
        } catch (Exception e) {
            String errorMsg = "Ошибка при получении деталей номера " + roomNumber + ": " + e.getMessage();
            logger.error(errorMsg, e);
            System.out.println("Ошибка при получении деталей номера: " + e.getMessage());
        }
    }

    public void printPriceListSortedByCategoryAndPrice() {
        logger.info("Начало вывода прайс-листа");
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

            logger.info("Прайс-лист выведен: {} номеров, {} услуг", rooms.size(), services.size());
        } catch (Exception e) {
            logger.error("Ошибка при выводе прайс-листа: {}", e.getMessage(), e);
            System.out.println("Ошибка при выводе прайс-листа: " + e.getMessage());
        }
    }

    public void printAllRoomsSortedByPrice() {
        logger.info("Начало вывода всех номеров отсортированных по цене");
        System.out.println("\n=== ВСЕ НОМЕРА (по цене) ===");
        List<Room> rooms = getRoomsSortedByPrice();
        if (rooms.isEmpty()) {
            String message = "Нет номеров в базе данных";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Тип: %s, Цена: %.2f руб., Вместимость: %d, Звезды: %d, Статус: %s%n",
                    room.getNumber(), room.getType(), room.getPrice(),
                    room.getCapacity(), room.getStars(), room.getStatus());
        }
        logger.info("Выведено {} номеров отсортированных по цене", rooms.size());
    }

    public void printAllRoomsSortedByCapacity() {
        logger.info("Начало вывода всех номеров отсортированных по вместимости");
        System.out.println("\n=== ВСЕ НОМЕРА (по вместимости) ===");
        List<Room> rooms = getRoomsSortedByCapacity();
        if (rooms.isEmpty()) {
            String message = "Нет номеров в базе данных";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Вместимость: %d, Тип: %s, Цена: %.2f руб., Звезды: %d%n",
                    room.getNumber(), room.getCapacity(), room.getType(),
                    room.getPrice(), room.getStars());
        }
        logger.info("Выведено {} номеров отсортированных по вместимости", rooms.size());
    }

    public void printAllRoomsSortedByStars() {
        logger.info("Начало вывода всех номеров отсортированных по звездам");
        System.out.println("\n=== ВСЕ НОМЕРА (по звездам) ===");
        List<Room> rooms = getRoomsSortedByStars();
        if (rooms.isEmpty()) {
            String message = "Нет номеров в базе данных";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Звезды: %d, Тип: %s, Цена: %.2f руб., Вместимость: %d%n",
                    room.getNumber(), room.getStars(), room.getType(),
                    room.getPrice(), room.getCapacity());
        }
        logger.info("Выведено {} номеров отсортированных по звездам", rooms.size());
    }

    public void printAvailableRoomsSortedByPrice() {
        logger.info("Начало вывода доступных номеров отсортированных по цене");
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по цене) ===");
        List<Room> rooms = getAvailableRoomsSortedByPrice();
        if (rooms.isEmpty()) {
            String message = "Нет свободных номеров";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Цена: %.2f руб., Тип: %s, Вместимость: %d, Звезды: %d%n",
                    room.getNumber(), room.getPrice(), room.getType(),
                    room.getCapacity(), room.getStars());
        }
        logger.info("Выведено {} доступных номеров отсортированных по цене", rooms.size());
    }

    public void printAvailableRoomsSortedByCapacity() {
        logger.info("Начало вывода доступных номеров отсортированных по вместимости");
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по вместимости) ===");
        List<Room> rooms = getAvailableRoomsSortedByCapacity();
        if (rooms.isEmpty()) {
            String message = "Нет свободных номеров";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Вместимость: %d, Цена: %.2f руб., Тип: %s, Звезды: %d%n",
                    room.getNumber(), room.getCapacity(), room.getPrice(),
                    room.getType(), room.getStars());
        }
        logger.info("Выведено {} доступных номеров отсортированных по вместимости", rooms.size());
    }

    public void printAvailableRoomsSortedByStars() {
        logger.info("Начало вывода доступных номеров отсортированных по звездам");
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (по звездам) ===");
        List<Room> rooms = getAvailableRoomsSortedByStars();
        if (rooms.isEmpty()) {
            String message = "Нет свободных номеров";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Звезды: %d, Цена: %.2f руб., Тип: %s, Вместимость: %d%n",
                    room.getNumber(), room.getStars(), room.getPrice(),
                    room.getType(), room.getCapacity());
        }
        logger.info("Выведено {} доступных номеров отсортированных по звездам", rooms.size());
    }

    public void printGuestsSortedByName() {
        logger.info("Начало вывода постояльцев отсортированных по имени");
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (по имени) ===");
        List<Booking> bookings = getGuestsAndRoomsSortedByName();
        if (bookings.isEmpty()) {
            String message = "Нет активных бронирований";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Booking booking : bookings) {
            System.out.printf("Гость: %s, Паспорт: %s, Комната: %s, Заезд: %s, Выезд: %s%n",
                    booking.getGuest().getName(), booking.getGuest().getPassportNumber(),
                    booking.getRoom().getNumber(), booking.getCheckInDate(), booking.getCheckOutDate());
        }
        logger.info("Выведено {} постояльцев отсортированных по имени", bookings.size());
    }

    public void printGuestsSortedByCheckoutDate() {
        logger.info("Начало вывода постояльцев отсортированных по дате выезда");
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (по дате выезда) ===");
        List<Booking> bookings = getGuestsAndRoomsSortedByCheckoutDate();
        if (bookings.isEmpty()) {
            String message = "Нет активных бронирований";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Booking booking : bookings) {
            System.out.printf("Выезд: %s, Гость: %s, Комната: %s, Заезд: %s%n",
                    booking.getCheckOutDate(), booking.getGuest().getName(),
                    booking.getRoom().getNumber(), booking.getCheckInDate());
        }
        logger.info("Выведено {} постояльцев отсортированных по дате выезда", bookings.size());
    }

    public void printRoomsAvailableByDate(LocalDate date) {
        logger.info("Начало вывода номеров доступных на дату: {}", date);
        System.out.println("\n=== НОМЕРА, СВОБОДНЫЕ НА ДАТУ " + date + " ===");
        List<Room> rooms = getRoomsAvailableByDate(date);
        if (rooms.isEmpty()) {
            String message = "Нет свободных номеров на эту дату";
            logger.info(message);
            System.out.println(message);
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Номер: %s, Тип: %s, Цена: %.2f руб., Вместимость: %d, Звезды: %d%n",
                    room.getNumber(), room.getType(), room.getPrice(),
                    room.getCapacity(), room.getStars());
        }
        logger.info("Выведено {} номеров доступных на дату {}", rooms.size(), date);
    }
}