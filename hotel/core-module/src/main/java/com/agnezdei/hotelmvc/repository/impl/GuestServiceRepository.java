package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class GuestServiceRepository extends BaseRepository implements GenericDAO<GuestService, Long> {
    
    public GuestServiceRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public GuestService save(GuestService guestService) throws DAOException {
        String sql = "INSERT INTO guest_service (guest_id, service_id, service_date) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setLong(1, guestService.getGuest().getId());
            stmt.setLong(2, guestService.getService().getId());
            stmt.setString(3, guestService.getServiceDate().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание заказа услуги не удалось");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                guestService.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание заказа не удалось, ID не получен");
            }
            
            return guestService;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении заказа услуги", e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<GuestService> findById(Long id) throws DAOException {
        String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToGuestService(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказа услуги по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<GuestService> findAll() throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "ORDER BY gs.service_date DESC, g.name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех заказов услуг", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guestServices;
    }
    
    @Override
    public void update(GuestService guestService) throws DAOException {
        String sql = "UPDATE guest_service SET guest_id = ?, service_id = ?, service_date = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setLong(1, guestService.getGuest().getId());
            stmt.setLong(2, guestService.getService().getId());
            stmt.setString(3, guestService.getServiceDate().toString());
            stmt.setLong(4, guestService.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Заказ услуги не найден для обновления: ID=" + guestService.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении заказа услуги: " + guestService.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM guest_service WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Заказ услуги не найден для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении заказа услуги: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public List<GuestService> findByGuestId(Long guestId) throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, s.* FROM guest_service gs " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.guest_id = ? ORDER BY gs.service_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, guestId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                guestServices.add(mapSimpleResultSetToGuestService(rs, guestId));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов услуг гостя: " + guestId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guestServices;
    }
    
    public List<GuestService> findByServiceId(Long serviceId) throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, g.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "WHERE gs.service_id = ? ORDER BY gs.service_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, serviceId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов для услуги: " + serviceId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guestServices;
    }
    
    public List<GuestService> findByDate(LocalDate date) throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.service_date = ? ORDER BY g.name";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, date.toString());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов услуг по дате: " + date, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guestServices;
    }
    
    public List<GuestService> findByGuestIdAndDateRange(Long guestId, LocalDate startDate, LocalDate endDate) 
            throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, s.* FROM guest_service gs " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.guest_id = ? AND gs.service_date BETWEEN ? AND ? " +
                     "ORDER BY gs.service_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, guestId);
            stmt.setString(2, startDate.toString());
            stmt.setString(3, endDate.toString());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                guestServices.add(mapSimpleResultSetToGuestService(rs, guestId));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов услуг гостя за период", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guestServices;
    }
    
    private GuestService mapResultSetToGuestService(ResultSet rs) throws SQLException {
        GuestService guestService = new GuestService();
        
        guestService.setId(rs.getLong("id"));
        guestService.setServiceDate(LocalDate.parse(rs.getString("service_date")));
        
        Guest guest = new Guest();
        guest.setId(rs.getLong("guest_id"));
        guest.setName(rs.getString("name"));
        guest.setPassportNumber(rs.getString("passport_number"));
        guestService.setGuest(guest);
        
        Service service = new Service();
        service.setId(rs.getLong("service_id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getDouble("price"));
        
        String categoryStr = rs.getString("category");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryStr));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }
        
        guestService.setService(service);
        
        return guestService;
    }
    
    private GuestService mapSimpleResultSetToGuestService(ResultSet rs, Long guestId) throws SQLException {
        GuestService guestService = new GuestService();
        
        guestService.setId(rs.getLong("id"));
        guestService.setServiceDate(LocalDate.parse(rs.getString("service_date")));
        
        Guest guest = new Guest();
        guest.setId(guestId);
        guestService.setGuest(guest);
        
        Service service = new Service();
        service.setId(rs.getLong("service_id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getDouble("price"));
        
        String categoryStr = rs.getString("category");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryStr));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }
        
        guestService.setService(service);
        
        return guestService;
    }
}