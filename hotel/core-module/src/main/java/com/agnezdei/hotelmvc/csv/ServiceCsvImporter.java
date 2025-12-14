package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.annotations.Inject;

public class ServiceCsvImporter {
    @Inject
    private Hotel hotel;

    public ServiceCsvImporter() {
    }

    public String importServices(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 4) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    String name = data[1];
                    double price = Double.parseDouble(data[2]);
                    ServiceCategory category = parseServiceCategory(data[3]);

                    Service existingService = hotel.findServiceById(id);
                    
                    if (existingService != null) {
                        existingService.setPrice(price);
                        updated++;
                    } else {
                        Service service = new Service(id, name, price, category, hotel);
                        hotel.addService(service);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт услуг завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                            imported, updated, errors.size());
    }

    private ServiceCategory parseServiceCategory(String categoryStr) {
        switch (categoryStr.toLowerCase()) {
            case "питание": return ServiceCategory.FOOD;
            case "обслуживание": return ServiceCategory.CLEANING;
            case "комфорт": return ServiceCategory.COMFORT;
            default: 
                try {
                    return ServiceCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Неизвестная категория услуги: " + categoryStr);
                }
        }
    }
}