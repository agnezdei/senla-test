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
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class GuestRepository extends BaseRepository implements GenericDAO<Guest, Long> {
    
    public GuestRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public Guest save(Guest guest) throws DAOException {
        String sql = "INSERT INTO guest (name, passport_number) VALUES (?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getPassportNumber());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание гостя не удалось, ни одна запись не добавлена");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                guest.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание гостя не удалось, ID не получен");
            }
            
            return guest;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении гостя: " + guest.getName(), e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<Guest> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM guest WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToGuest(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостя по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Guest> findAll() throws DAOException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guest ORDER BY name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех гостей", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guests;
    }
    
    @Override
    public void update(Guest guest) throws DAOException {
        String sql = "UPDATE guest SET name = ?, passport_number = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getPassportNumber());
            stmt.setLong(3, guest.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Гость не найден для обновления: ID=" + guest.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении гостя: " + guest.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM guest WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Гость не найдена для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении гостя: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public Optional<Guest> findByPassportNumber(String passportNumber) throws DAOException {
        String sql = "SELECT * FROM guest WHERE passport_number = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, passportNumber);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToGuest(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостя по номеру паспорта: " + passportNumber, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public List<Guest> findGuestsWithActiveBookings() throws DAOException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT DISTINCT g.* FROM guest g " +
                    "JOIN booking b ON g.id = b.guest_id " +
                    "WHERE b.is_active = TRUE ORDER BY g.name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостей с активными бронированиями", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return guests;
    }
    
    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setName(rs.getString("name"));
        guest.setPassportNumber(rs.getString("passport_number"));
        return guest;
    }
}