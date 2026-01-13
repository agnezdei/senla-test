package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.impl.GuestRepository;

public class GuestCsvImporter {
    @Inject
    private GuestRepository guestDAO;

    public GuestCsvImporter() {
    }

  public String importGuests(String filePath) {
    List<String> errors = new ArrayList<>();
    int imported = 0;
    int updated = 0;
    
    System.out.println("=== Начало импорта гостей ===");
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line = reader.readLine();
        System.out.println("Заголовок: " + line);
        
        int lineNum = 1;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            System.out.println("Строка " + lineNum + ": " + line);
            
            try {
                String[] data = line.split(",");
                System.out.println("Количество полей: " + data.length);
                
                if (data.length < 2) {
                    String error = "Строка " + lineNum + ": Недостаточно данных";
                    errors.add(error);
                    System.out.println("ОШИБКА: " + error);
                    continue;
                }
                
                String name = data[0].trim();
                String passportNumber = data[1].trim();
                
                System.out.println("Парсинг: имя='" + name + "', паспорт='" + passportNumber + "'");
                
                // Ищем гостя по паспорту
                Optional<Guest> existingGuest = guestDAO.findByPassportNumber(passportNumber);
                
                if (existingGuest.isPresent()) {
                    System.out.println("Гость с паспортом " + passportNumber + " уже существует");
                    Guest guest = existingGuest.get();
                    if (!guest.getName().equals(name)) {
                        guest.setName(name);
                        guestDAO.update(guest);
                        updated++;
                        System.out.println("Обновлен: " + name);
                    }
                } else {
                    System.out.println("Создаем нового гостя: " + name);
                    Guest guest = new Guest();
                    guest.setName(name);
                    guest.setPassportNumber(passportNumber);
                    
                    guestDAO.save(guest);
                    imported++;
                    System.out.println("Добавлен: " + name);
                }
                
            } catch (Exception e) {
                String error = "Строка " + lineNum + ": " + e.getMessage() + " - " + line;
                errors.add(error);
                System.out.println("ОШИБКА: " + error);
                e.printStackTrace();
            }
        }
        
    } catch (IOException e) {
        return "Ошибка чтения файла: " + e.getMessage();
    }
    
    System.out.println("=== Импорт гостей завершен ===");
    return String.format("Импорт гостей завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                        imported, updated, errors.size());
}
}