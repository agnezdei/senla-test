package com.agnezdei.hotelmvc.csv;

import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.GuestDAO;
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
public class GuestCsvImporter {

    @Autowired
    private GuestDAO guestDAO;

    @Transactional
    public String importGuests(String filePath) {
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
                    if (data.length < 2) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 2)");
                        continue;
                    }

                    String name = data[0].trim();
                    String passportNumber = data[1].trim();

                    Optional<Guest> existingGuestOpt = guestDAO.findByPassportNumber(passportNumber);

                    if (existingGuestOpt.isPresent()) {
                        Guest existingGuest = existingGuestOpt.get();
                        // обновляем только если имя изменилось
                        if (!existingGuest.getName().equals(name)) {
                            existingGuest.setName(name);
                            guestDAO.update(existingGuest);
                            updated++;
                        }
                    } else {
                        Guest newGuest = new Guest();
                        newGuest.setName(name);
                        newGuest.setPassportNumber(passportNumber);
                        guestDAO.save(newGuest);
                        imported++;
                    }

                } catch (Exception e) {
                    errors.add("Строка " + lineNum + ": " + e.getMessage() + " - " + line);
                }
            }

        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт гостей завершен: %d добавлено, %d обновлено. Ошибок: %d",
                imported, updated, errors.size());
    }
}