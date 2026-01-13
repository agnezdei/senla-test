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
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.BookingService;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class BookingServiceRepository extends BaseRepository implements GenericDAO<BookingService, Long> {
    
    public BookingServiceRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public BookingService save(BookingService bookingService) throws DAOException {
        String sql = "INSERT INTO booking_service (booking_id, service_id, service_date) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setLong(1, bookingService.getBooking().getId());
            stmt.setLong(2, bookingService.getService().getId());
            stmt.setString(3, bookingService.getServiceDate().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание связи услуги с бронированием не удалось");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                bookingService.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание связи не удалось, ID не получен");
            }
            
            return bookingService;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении связи услуги с бронированием", e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<BookingService> findById(Long id) throws DAOException {
        String sql = "SELECT bs.*, b.*, s.* FROM booking_service bs " +
                     "JOIN booking b ON bs.booking_id = b.id " +
                     "JOIN service s ON bs.service_id = s.id " +
                     "WHERE bs.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToBookingService(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске связи по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<BookingService> findAll() throws DAOException {
        List<BookingService> bookingServices = new ArrayList<>();
        String sql = "SELECT bs.*, b.*, s.* FROM booking_service bs " +
                     "JOIN booking b ON bs.booking_id = b.id " +
                     "JOIN service s ON bs.service_id = s.id " +
                     "ORDER BY bs.booking_id, bs.service_date";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                bookingServices.add(mapResultSetToBookingService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех связей услуг с бронированиями", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookingServices;
    }
    
    @Override
    public void update(BookingService bookingService) throws DAOException {
        String sql = "UPDATE booking_service SET booking_id = ?, service_id = ?, service_date = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setLong(1, bookingService.getBooking().getId());
            stmt.setLong(2, bookingService.getService().getId());
            stmt.setString(3, bookingService.getServiceDate().toString());
            stmt.setLong(4, bookingService.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Связь не найдена для обновления: ID=" + bookingService.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении связи услуги с бронированием: " + bookingService.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM booking_service WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Связь не найдена для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении связи услуги с бронированием: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public List<BookingService> findByBookingId(Long bookingId) throws DAOException {
        List<BookingService> bookingServices = new ArrayList<>();
        String sql = "SELECT bs.*, s.* FROM booking_service bs " +
                     "JOIN service s ON bs.service_id = s.id " +
                     "WHERE bs.booking_id = ? ORDER BY bs.service_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, bookingId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                bookingServices.add(mapSimpleResultSetToBookingService(rs, bookingId));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуг для бронирования: " + bookingId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookingServices;
    }
    
    public List<BookingService> findByServiceId(Long serviceId) throws DAOException {
        List<BookingService> bookingServices = new ArrayList<>();
        String sql = "SELECT bs.*, b.* FROM booking_service bs " +
                     "JOIN booking b ON bs.booking_id = b.id " +
                     "WHERE bs.service_id = ? ORDER BY bs.service_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, serviceId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                bookingServices.add(mapResultSetToBookingService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований для услуги: " + serviceId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookingServices;
    }
    
    public void deleteByBookingId(Long bookingId) throws DAOException {
        String sql = "DELETE FROM booking_service WHERE booking_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, bookingId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении услуг для бронирования: " + bookingId, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    private BookingService mapResultSetToBookingService(ResultSet rs) throws SQLException {
        BookingService bookingService = new BookingService();
        
        bookingService.setId(rs.getLong("id"));
        bookingService.setServiceDate(LocalDate.parse(rs.getString("service_date")));
        
        Booking booking = new Booking();
        booking.setId(rs.getLong("booking_id"));
        bookingService.setBooking(booking);
        
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
        
        bookingService.setService(service);
        
        return bookingService;
    }
    
    private BookingService mapSimpleResultSetToBookingService(ResultSet rs, Long bookingId) throws SQLException {
        BookingService bookingService = new BookingService();
        
        bookingService.setId(rs.getLong("id"));
        bookingService.setServiceDate(LocalDate.parse(rs.getString("service_date")));
        
        Booking booking = new Booking();
        booking.setId(bookingId);
        bookingService.setBooking(booking);
        
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
        
        bookingService.setService(service);
        
        return bookingService;
    }
}