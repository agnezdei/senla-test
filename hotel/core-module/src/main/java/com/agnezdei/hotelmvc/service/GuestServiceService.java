package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestServiceCsvImporter;
import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.GuestServiceMapper;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GuestServiceService {
    private static final Logger logger = LoggerFactory.getLogger(GuestServiceService.class);

    private final GuestServiceDAO guestServiceDAO;
    private final GuestDAO guestDAO;
    private final CsvExporter csvExporter;
    private final GuestServiceCsvImporter guestServiceImporter;
    private final ServiceService serviceService;

    @Autowired
    public GuestServiceService(GuestServiceDAO guestServiceDAO,
                               GuestDAO guestDAO,
                               CsvExporter csvExporter,
                               GuestServiceCsvImporter guestServiceImporter,
                               ServiceService serviceService) {
        this.guestServiceDAO = guestServiceDAO;
        this.guestDAO = guestDAO;
        this.csvExporter = csvExporter;
        this.guestServiceImporter = guestServiceImporter;
        this.serviceService = serviceService;
    }

    @Transactional(readOnly = true)
    public String exportToCsv(String filePath) {
        logger.info("Начало экспорта услуг гостей в файл: {}", filePath);
        try {
            List<GuestService> guestServices = guestServiceDAO.findAll();
            List<GuestServiceDTO> guestServiceDTOs = GuestServiceMapper.toDTOList(guestServices);
            csvExporter.exportGuestServices(guestServiceDTOs, filePath);
            String result = "Успех: Услуги гостей экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте услуг гостей: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте услуг гостей: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка БД при экспорте услуг гостей: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте услуг гостей: " + e.getMessage();
        }
    }

    public String importFromCsv(String filePath) {
        logger.info("Начало импорта услуг гостей из файла: {}", filePath);
        try {
            String result = guestServiceImporter.importGuestServices(filePath);
            logger.info("Импорт услуг гостей завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта услуг гостей: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String addServiceToGuest(String guestPassport, String serviceName, LocalDate serviceDate)
            throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало добавления услуги гостю: паспорт={}, услуга={}, дата={}",
                guestPassport, serviceName, serviceDate);

        Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport);
        if (guestOpt.isEmpty()) {
            throw new EntityNotFoundException("Гость с паспортом " + guestPassport + " не найден");
        }

        Optional<com.agnezdei.hotelmvc.model.Service> serviceOpt = serviceService.findByName(serviceName);
        if (serviceOpt.isEmpty()) {
            throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
        }

        Guest guest = guestOpt.get();
        com.agnezdei.hotelmvc.model.Service service = serviceOpt.get();

        GuestService guestServiceEntity = new GuestService(guest, service, serviceDate);
        guestServiceDAO.save(guestServiceEntity);

        String result = "Успех: Услуга '" + serviceName + "' добавлена гостю " + guest.getName();
        logger.info(result);
        return result;
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String removeServiceFromGuest(Long guestServiceId)
            throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало удаления услуги гостя: ID={}", guestServiceId);

        Optional<GuestService> guestServiceOpt = guestServiceDAO.findById(guestServiceId);
        if (guestServiceOpt.isEmpty()) {
            throw new EntityNotFoundException("Заказ услуги не найден: ID=" + guestServiceId);
        }

        guestServiceDAO.deleteById(guestServiceId);

        String result = "Успех: Услуга удалена из заказов гостя";
        logger.info(result);
        return result;
    }

    @Transactional(readOnly = true)
    public List<GuestServiceDTO> getGuestServices(Long guestId) throws BusinessLogicException {
        logger.info("Начало получения услуг гостя: ID={}", guestId);
        try {
            List<GuestService> services = guestServiceDAO.findByGuestId(guestId);
            logger.info("Получено услуг для гостя ID={}: {}", guestId, services.size());
            return GuestServiceMapper.toDTOList(services);
        } catch (Exception e) {
            logger.error("Ошибка БД при получении услуг гостя: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<GuestServiceDTO> getGuestServicesByName(String guestName) throws BusinessLogicException {
        logger.info("Начало получения услуг гостя по имени: {}", guestName);
        try {
            List<GuestService> services = guestServiceDAO.findByGuestNameOrderedByDate(guestName);
            logger.info("Получено услуг для гостя {}: {}", guestName, services.size());
            return GuestServiceMapper.toDTOList(services);
        } catch (Exception e) {
            logger.error("Ошибка БД при получении услуг гостя по имени: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<GuestService> getGuestServicesByNameSortedByPrice(String guestName) {
        return guestServiceDAO.findByGuestNameOrderedByPrice(guestName);
    }

    @Transactional(readOnly = true)
    public List<GuestService> getGuestServicesByNameSortedByDate(String guestName) {
        return guestServiceDAO.findByGuestNameOrderedByDate(guestName);
    }
}