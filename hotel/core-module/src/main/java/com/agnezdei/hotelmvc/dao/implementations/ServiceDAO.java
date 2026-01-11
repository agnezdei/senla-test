package com.agnezdei.hotelmvc.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.agnezdei.hotelmvc.dao.interfaces.GenericDAO;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;

public class ServiceDAO extends BaseDAO implements GenericDAO<Service, Long> {
    
    private Map<String, Integer> categoryCache = new HashMap<>();
    
    public ServiceDAO() {
        initializeCategoryCache();
    }
    
    private void initializeCategoryCache() {
        String sql = "SELECT id, name FROM service_category";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categoryCache.put(rs.getString("name"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки кэша категорий: " + e.getMessage());
        }
    }
    
    @Override
    public Service save(Service service) throws DAOException {
        String sql = "INSERT INTO service (name, price, category_id) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            Integer categoryId = categoryCache.get(service.getCategory().name());
            if (categoryId == null) {
                throw new DAOException("Категория услуги не найдена: " + service.getCategory());
            }
            
            stmt.setString(1, service.getName());
            stmt.setDouble(2, service.getPrice());
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setId(generatedKeys.getLong(1));
                }
            }
            return service;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении услуги: " + service.getName(), e);
        }
    }
    
    @Override
    public Optional<Service> findById(Long id) throws DAOException {
        String sql = """
            SELECT s.*, sc.name as category_name 
            FROM service s
            JOIN service_category sc ON s.category_id = sc.id
            WHERE s.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToService(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по ID: " + id, e);
        }
    }
    
    @Override
    public List<Service> findAll() throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.*, sc.name as category_name 
            FROM service s
            JOIN service_category sc ON s.category_id = sc.id
            ORDER BY s.name
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех услуг", e);
        }
    }
    
    @Override
    public void update(Service service) throws DAOException {
        String sql = "UPDATE service SET name = ?, price = ?, category_id = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            Integer categoryId = categoryCache.get(service.getCategory().name());
            if (categoryId == null) {
                throw new DAOException("Категория услуги не найдена: " + service.getCategory());
            }
            
            stmt.setString(1, service.getName());
            stmt.setDouble(2, service.getPrice());
            stmt.setInt(3, categoryId);
            stmt.setLong(4, service.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Услуга не найдена для обновления: ID=" + service.getId());
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении услуги: " + service.getId(), e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM service WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении услуги: " + id, e);
        }
    }
    
    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getDouble("price"));
        
        String categoryName = rs.getString("category_name");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryName));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }
        return service;
    }
    
    public List<Service> findByCategory(ServiceCategory category) throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.*, sc.name as category_name 
            FROM service s
            JOIN service_category sc ON s.category_id = sc.id
            WHERE sc.name = ?
            ORDER BY s.price
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуг по категории: " + category, e);
        }
    }
    
    public Optional<Service> findByName(String name) throws DAOException {
        String sql = "SELECT s.*, sc.name as category_name FROM service s " +
                     "JOIN service_category sc ON s.category_id = sc.id " +
                     "WHERE s.name = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToService(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по имени: " + name, e);
        }
    }
}