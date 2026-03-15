package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.dto.ServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.ServiceMapper;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.ServiceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ServiceService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceService.class);

    @Autowired
    private ServiceDAO serviceDAO;
    @Autowired
    private CsvExporter csvExporter;
    @Autowired
    private ServiceCsvImporter serviceImporter;

    @Transactional(readOnly = true)
    public String exportToCsv(String filePath) {
        logger.info("Начало экспорта услуг в файл: {}", filePath);
        try {
            List<com.agnezdei.hotelmvc.model.Service> services = serviceDAO.findAll();
            List<ServiceDTO> serviceDTOs = ServiceMapper.toDTOList(services);
            csvExporter.exportServices(serviceDTOs, filePath);
            String result = "Успех: Услуги экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте услуг: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте услуг: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка БД при экспорте услуг: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте услуг: " + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String importFromCsv(String filePath) {
        logger.info("Начало импорта услуг из файла: {}", filePath);
        try {
            String result = serviceImporter.importServices(filePath);
            logger.info("Импорт услуг завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта услуг: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = BusinessLogicException.class)
    public String addService(String name, double price, ServiceCategory category) throws BusinessLogicException {
        logger.info("Начало добавления услуги: название={}, цена={}, категория={}", name, price, category);

        Optional<com.agnezdei.hotelmvc.model.Service> existingService = serviceDAO.findByName(name);
        if (existingService.isPresent()) {
            throw new BusinessLogicException("Услуга '" + name + "' уже существует");
        }

        com.agnezdei.hotelmvc.model.Service service = new com.agnezdei.hotelmvc.model.Service(name, price, category);
        serviceDAO.save(service);

        String result = "Успех: Добавлена услуга '" + name + "'";
        logger.info(result);
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String changePrice(String serviceName, double newPrice) throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало изменения цены услуги: услуга={}, новая цена={}", serviceName, newPrice);

        Optional<com.agnezdei.hotelmvc.model.Service> serviceOpt = serviceDAO.findByName(serviceName);
        if (serviceOpt.isEmpty()) {
            throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
        }

        com.agnezdei.hotelmvc.model.Service service = serviceOpt.get();
        service.setPrice(newPrice);
        serviceDAO.update(service);

        String result = "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
        logger.info(result);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Service> getAllServices() {
        return serviceDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Service> getAllServicesSortedByCategoryAndPrice() {
        logger.debug("Запрос всех услуг, отсортированных по категории и цене");
        return serviceDAO.findAllOrderedByCategoryAndPrice();
    }

    @Transactional(readOnly = true)
    public Optional<com.agnezdei.hotelmvc.model.Service> findByName(String name) {
        return serviceDAO.findByName(name);
    }
}