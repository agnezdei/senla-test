package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.annotations.Inject;

public class GuestCsvImporter {
    @Inject
    private Hotel hotel;

    public GuestCsvImporter() {
    }

    public String importGuests(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 3) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    String name = data[1];
                    String passportNumber = data[2];

                    Guest existingGuest = hotel.findGuestById(id);
                    
                    if (existingGuest != null) {
                        updated++;
                    } else {
                        Guest guest = new Guest(id, name, passportNumber);
                        hotel.addGuest(guest);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт гостей завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                            imported, updated, errors.size());
    }
}