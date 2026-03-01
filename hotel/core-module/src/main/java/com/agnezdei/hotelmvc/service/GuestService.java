package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class GuestService {
    private static final Logger logger = LoggerFactory.getLogger(GuestService.class);

    @Autowired
    private GuestDAO guestDAO;
    @Autowired
    private CsvExporter csvExporter;
    @Autowired
    private GuestCsvImporter guestImporter;

    @Transactional(readOnly = true)
    public String exportToCsv(String filePath) {
        logger.info("Начало экспорта гостей в файл: {}", filePath);
        try {
            List<Guest> guests = guestDAO.findAll();
            List<GuestDTO> guestDTOs = GuestMapper.toDTOList(guests);
            csvExporter.exportGuests(guestDTOs, filePath);
            String result = "Успех: Гости экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (IOException e) {
            logger.error("Ошибка БД при экспорте гостей: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте гостей: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка файла при экспорте гостей: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте гостей: " + e.getMessage();
        }
    }

    public String importFromCsv(String filePath) {
        logger.info("Начало импорта гостей из файла: {}", filePath);
        try {
            String result = guestImporter.importGuests(filePath);
            logger.info("Импорт гостей завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта гостей: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Guest> findByPassportNumber(String passportNumber) {
        return guestDAO.findByPassportNumber(passportNumber);
    }

    @Transactional
    public Guest save(Guest guest) {
        return guestDAO.save(guest);
    }

    @Transactional
    public void update(Guest guest) {
        guestDAO.update(guest);
    }
}