package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.util.HibernateUtil;

@Component
public class GuestCsvImporter {
    @Autowired
    private GuestDAO guestDAO;

    public GuestCsvImporter() {
    }

    public String importGuests(String filePath) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            String result = importGuests(filePath, session);
            
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return "Ошибка при импорте гостей: " + e.getMessage();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String importGuests(String filePath, Session session) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 2) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных");
                        continue;
                    }

                    String name = data[0].trim();
                    String passportNumber = data[1].trim();

                    Optional<Guest> existingGuest = guestDAO.findByPassportNumber(passportNumber, session);

                    if (existingGuest.isPresent()) {
                        Guest guest = existingGuest.get();
                        if (!guest.getName().equals(name)) {
                            guest.setName(name);
                            guestDAO.update(guest, session);
                            updated++;
                        }
                    } else {
                        Guest guest = new Guest();
                        guest.setName(name);
                        guest.setPassportNumber(passportNumber);

                        guestDAO.save(guest, session);
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