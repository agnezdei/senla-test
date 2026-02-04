package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import com.agnezdei.hotelmvc.repository.ServiceDAO;

public class GuestServiceCsvImporter {
    @Inject
    private GuestServiceDAO guestServiceDAO;
    @Inject
    private GuestDAO guestDAO;
    @Inject
    private ServiceDAO serviceDAO;

    public GuestServiceCsvImporter() {
    }

    public String importGuestServices(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;

                try {
                    String[] data = line.split(",");

                    if (data.length < 3) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 3)");
                        continue;
                    }

                    String guestPassport = data[0].trim();
                    String serviceName = data[1].trim();
                    LocalDate serviceDate = LocalDate.parse(data[2].trim());

                    Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport);
                    if (guestOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Гость с паспортом " + guestPassport + " не найден");
                        continue;
                    }

                    Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
                    if (serviceOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Услуга '" + serviceName + "' не найдена");
                        continue;
                    }

                    Guest guest = guestOpt.get();
                    Service service = serviceOpt.get();

                    List<GuestService> existingServices = guestServiceDAO.findByGuestId(guest.getId());
                    boolean alreadyExists = false;

                    for (GuestService gs : existingServices) {
                        if (gs.getService().getId().equals(service.getId()) &&
                                gs.getServiceDate().equals(serviceDate)) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (!alreadyExists) {
                        GuestService guestService = new GuestService();
                        guestService.setGuest(guest);
                        guestService.setService(service);
                        guestService.setServiceDate(serviceDate);

                        guestServiceDAO.save(guestService);
                        imported++;
                    }

                } catch (Exception e) {
                    errors.add("Строка " + lineNum + ": " + e.getMessage() + " - " + line);
                }
            }

        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт заказов услуг завершен: %d добавлено. Ошибок: %d",
                imported, errors.size());
    }
}