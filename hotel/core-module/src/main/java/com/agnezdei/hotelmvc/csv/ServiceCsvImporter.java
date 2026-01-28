package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;

public class ServiceCsvImporter {
    @Inject
    private ServiceRepository serviceDAO;

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
                    if (data.length < 3) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    String name = data[0];
                    double price = Double.parseDouble(data[1]);
                    ServiceCategory category = parseServiceCategory(data[2]);

                    Optional<Service> existingService = serviceDAO.findByName(name);

                    if (existingService.isPresent()) {
                        Service service = existingService.get();
                        service.setPrice(price);

                        serviceDAO.update(service);
                        updated++;
                    } else {
                        Service service = new Service();
                        service.setName(name);
                        service.setPrice(price);
                        service.setCategory(category);

                        serviceDAO.save(service);
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
        if (categoryStr == null)
            return ServiceCategory.COMFORT;

        switch (categoryStr.toLowerCase().trim()) {
            case "питание":
            case "food":
                return ServiceCategory.FOOD;
            case "обслуживание":
            case "cleaning":
                return ServiceCategory.CLEANING;
            case "комфорт":
            case "comfort":
                return ServiceCategory.COMFORT;
            default:
                try {
                    return ServiceCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Неизвестная категория услуги: " + categoryStr + ", используем COMFORT");
                    return ServiceCategory.COMFORT;
                }
        }
    }
}