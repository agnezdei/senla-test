package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class ServiceRepository extends BaseRepository implements GenericDAO<Service, Long> {
    
    public ServiceRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public Service save(Service service) throws DAOException {
        String sql = "INSERT INTO service (name, price, category) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, service.getName());
            stmt.setDouble(2, service.getPrice());
            stmt.setString(3, service.getCategory().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание услуги не удалось, ни одна запись не добавлена");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                service.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание услуги не удалось, ID не получен");
            }
            
            return service;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении услуги: " + service.getName(), e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<Service> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM service WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToService(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Service> findAll() throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service ORDER BY name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех услуг", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return services;
    }
    
    @Override
    public void update(Service service) throws DAOException {
        String sql = "UPDATE service SET name = ?, price = ?, category = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, service.getName());
            stmt.setDouble(2, service.getPrice());
            stmt.setString(3, service.getCategory().name());
            stmt.setLong(4, service.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Услуга не найдена для обновления: ID=" + service.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении услуги: " + service.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM service WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Услуга не найдена для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении услуги: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public Optional<Service> findByName(String name) throws DAOException {
        String sql = "SELECT * FROM service WHERE name = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToService(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по названию: " + name, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public List<Service> findByCategory(ServiceCategory category) throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE category = ? ORDER BY name";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category.name());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуг по категории: " + category, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return services;
    }
    
    public List<Service> findByIds(List<Long> ids) throws DAOException {
        List<Service> services = new ArrayList<>();
        if (ids.isEmpty()) {
            return services;
        }
        
        StringBuilder sql = new StringBuilder("SELECT * FROM service WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) sql.append(",");
        }
        sql.append(") ORDER BY name");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < ids.size(); i++) {
                stmt.setLong(i + 1, ids.get(i));
            }
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуг по списку ID", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return services;
    }
    
    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        
        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getDouble("price"));
        
        String categoryStr = rs.getString("category");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryStr));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }
        
        return service;
    }
}