

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
import com.agnezdei.hotelmvc.model.Hotel;

public class TestImportFromDataFolder {
    public static void main(String[] args) {
        try {
            System.out.println("=== Тестирование импорта из папки data/ ===");
            
            // 1. Создаем DI контейнер
            DependencyContainer container = new DependencyContainer();
            
            // 2. Создаем и регистрируем DAO
            RoomDAO roomDAO = new RoomDAO();
            GuestDAO guestDAO = new GuestDAO();
            ServiceDAO serviceDAO = new ServiceDAO();
            BookingDAO bookingDAO = new BookingDAO();
            
            container.register(RoomDAO.class, roomDAO);
            container.register(GuestDAO.class, guestDAO);
            container.register(ServiceDAO.class, serviceDAO);
            container.register(BookingDAO.class, bookingDAO);
            
            // 3. Создаем и регистрируем импортеры
            RoomCsvImporter roomImporter = new RoomCsvImporter();
            GuestCsvImporter guestImporter = new GuestCsvImporter();
            ServiceCsvImporter serviceImporter = new ServiceCsvImporter();
            BookingCsvImporter bookingImporter = new BookingCsvImporter();
            CsvExporter csvExporter = new CsvExporter();
            
            container.register(RoomCsvImporter.class, roomImporter);
            container.register(GuestCsvImporter.class, guestImporter);
            container.register(ServiceCsvImporter.class, serviceImporter);
            container.register(BookingCsvImporter.class, bookingImporter);
            container.register(CsvExporter.class, csvExporter);
            
            // 4. Регистрируем остальное
            container.register(Hotel.class, new Hotel());
            container.register(AppConfig.class, new AppConfig());
            
            // 5. Внедряем зависимости
            container.inject(roomImporter);
            container.inject(guestImporter);
            container.inject(serviceImporter);
            container.inject(bookingImporter);
            container.inject(csvExporter);
            
            // 6. Создаем HotelAdmin
            HotelAdmin admin = container.create(HotelAdmin.class);
            
            // 7. Проверяем текущее состояние БД
            System.out.println("\nТекущее состояние БД:");
            try {
                System.out.println("Комнат: " + roomDAO.findAll().size());
                System.out.println("Гостей: " + guestDAO.findAll().size());
                System.out.println("Услуг: " + serviceDAO.findAll().size());
                System.out.println("Бронирований: " + bookingDAO.findAll().size());
            } catch (Exception e) {
                System.out.println("Ошибка при чтении из БД: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 8. Импортируем данные (только если нет ошибок)
            System.out.println("\n=== Импорт данных ===");
            
            // Импортируем комнаты
            try {
                System.out.println("\n1. Импорт комнат...");
                String result = admin.importRoomsFromCsv("data/rooms.csv");
                System.out.println("Результат: " + result);
            } catch (Exception e) {
                System.out.println("Ошибка импорта комнат: " + e.getMessage());
            }
            
            // Импортируем гостей
            try {
                System.out.println("\n2. Импорт гостей...");
                String result = admin.importGuestsFromCsv("data/guests.csv");
                System.out.println("Результат: " + result);
            } catch (Exception e) {
                System.out.println("Ошибка импорта гостей: " + e.getMessage());
            }
            
            // Импортируем услуги
            try {
                System.out.println("\n3. Импорт услуг...");
                String result = admin.importServicesFromCsv("data/services.csv");
                System.out.println("Результат: " + result);
            } catch (Exception e) {
                System.out.println("Ошибка импорта услуг: " + e.getMessage());
            }
            
            // Импортируем бронирования (может быть проблема с BookingDAO)
            try {
                System.out.println("\n4. Импорт бронирований...");
                String result = admin.importBookingsFromCsv("data/bookings.csv");
                System.out.println("Результат: " + result);
            } catch (Exception e) {
                System.out.println("Ошибка импорта бронирований: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 9. Проверяем итоговое состояние
            System.out.println("\n=== Итоговое состояние БД ===");
            try {
                System.out.println("Комнат: " + roomDAO.findAll().size());
                System.out.println("Гостей: " + guestDAO.findAll().size());
                System.out.println("Услуг: " + serviceDAO.findAll().size());
                // BookingDAO может быть сломан - проверим отдельно
                try {
                    System.out.println("Бронирований: " + bookingDAO.findAll().size());
                } catch (Exception e) {
                    System.out.println("Бронирований: ошибка чтения (" + e.getMessage() + ")");
                }
            } catch (Exception e) {
                System.out.println("Общая ошибка при чтении БД: " + e.getMessage());
            }
            
            System.out.println("\n=== Тест завершен ===");
            
        } catch (Exception e) {
            System.err.println("Общая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}