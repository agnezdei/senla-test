

import java.time.LocalDate;

import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.controller.HotelAdmin;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.dao.implementations.BookingDAO;
import com.agnezdei.hotelmvc.dao.implementations.GuestDAO;
import com.agnezdei.hotelmvc.dao.implementations.RoomDAO;
import com.agnezdei.hotelmvc.dao.implementations.ServiceDAO;
import com.agnezdei.hotelmvc.di.DependencyContainer;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.ServiceCategory;

public class TestHotelAdmin {
    public static void main(String[] args) {
        try {
            System.out.println("=== Тестирование HotelAdmin с DAO ===");
            
            DependencyContainer container = new DependencyContainer();
            
            RoomDAO roomDAO = new RoomDAO();
            GuestDAO guestDAO = new GuestDAO();
            ServiceDAO serviceDAO = new ServiceDAO();
            BookingDAO bookingDAO = new BookingDAO();
            
            container.register(RoomDAO.class, roomDAO);
            container.register(GuestDAO.class, guestDAO);
            container.register(ServiceDAO.class, serviceDAO);
            container.register(BookingDAO.class, bookingDAO);
            
            container.register(AppConfig.class, new AppConfig());
            container.register(CsvExporter.class, new CsvExporter());
            container.register(RoomCsvImporter.class, new RoomCsvImporter());
            container.register(GuestCsvImporter.class, new GuestCsvImporter());
            container.register(ServiceCsvImporter.class, new ServiceCsvImporter());
            container.register(BookingCsvImporter.class, new BookingCsvImporter());
            
            container.inject(new RoomCsvImporter());
            container.inject(new GuestCsvImporter());
            container.inject(new ServiceCsvImporter());
            container.inject(new BookingCsvImporter());
            container.inject(new CsvExporter());
            
            HotelAdmin admin = container.create(HotelAdmin.class);
            
            System.out.println("\n1. Тест добавления комнаты:");
            try {
                String result = admin.addRoom("999", RoomType.STANDARD, 3000.0, 2, 3);
                System.out.println(result);
                
                var roomOpt = roomDAO.findByNumber("999");
                if (roomOpt.isPresent()) {
                    System.out.println("✅ Комната успешно добавлена: ID=" + roomOpt.get().getId());
                }
            } catch (Exception e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
            
            System.out.println("\n2. Тест заселения гостя:");
            try {
                Guest guest = new Guest();
                guest.setName("Тестовый Гость");
                guest.setPassportNumber("999999");
                
                LocalDate checkIn = LocalDate.now();
                LocalDate checkOut = checkIn.plusDays(3);
                
                String result = admin.settleGuest("201", guest, checkIn, checkOut);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
            
            System.out.println("\n3. Тест добавления услуги:");
            try {
                String result = admin.addService("Тестовая услуга", 1000.0, ServiceCategory.COMFORT);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
            
            System.out.println("\n4. Тест изменения цены комнаты:");
            try {
                String result = admin.changeRoomPrice("101", 3500.0);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
            
            System.out.println("\n=== Тест завершен ===");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}