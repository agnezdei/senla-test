package com.agnezdei.hotelmvc.csv;

import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.ServiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ServiceCsvImporter {

    @Autowired
    private ServiceDAO serviceDAO;

    private ServiceCategory parseServiceCategory(String categoryStr) {
        if (categoryStr == null) return ServiceCategory.COMFORT;

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

    @Transactional
    public String importServices(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // пропускаем заголовок

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 3) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 3)");
                        continue;
                    }

                    String name = data[0].trim();
                    double price = Double.parseDouble(data[1].trim());
                    ServiceCategory category = parseServiceCategory(data[2].trim());

                    Optional<Service> existingServiceOpt = serviceDAO.findByName(name);

                    if (existingServiceOpt.isPresent()) {
                        Service service = existingServiceOpt.get();
                        service.setPrice(price);
                        // Категория обычно не меняется, но можно и её обновлять, если нужно
                        // service.setCategory(category);
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
                    errors.add("Строка " + lineNum + ": " + e.getMessage() + " - " + line);
                }
            }

        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт услуг завершен: %d добавлено, %d обновлено. Ошибок: %d",
                imported, updated, errors.size());
    }
}