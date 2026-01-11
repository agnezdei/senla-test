package com.agnezdei.hotelmvc.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.dao.interfaces.GenericDAO;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Guest;

public class GuestDAO extends BaseDAO implements GenericDAO<Guest, Long> {
    
    @Override
    public Guest save(Guest guest) throws DAOException {
        String sql = "INSERT INTO guest (name, passport_number) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getPassportNumber());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    guest.setId(generatedKeys.getLong(1));
                }
            }
            return guest;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении гостя: " + guest.getName(), e);
        }
    }
    
    @Override
    public Optional<Guest> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM guest WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuest(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостя по ID: " + id, e);
        }
    }
    
    @Override
    public List<Guest> findAll() throws DAOException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guest ORDER BY name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            return guests;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех гостей", e);
        }
    }
    
    @Override
    public void update(Guest guest) throws DAOException {
        String sql = "UPDATE guest SET name = ?, passport_number = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getPassportNumber());
            stmt.setLong(3, guest.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Гость не найден для обновления: ID=" + guest.getId());
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении гостя: " + guest.getId(), e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM guest WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении гостя: " + id, e);
        }
    }
    
    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setName(rs.getString("name"));
        guest.setPassportNumber(rs.getString("passport_number"));
        return guest;
    }
    
    public Optional<Guest> findByPassportNumber(String passportNumber) throws DAOException {
        String sql = "SELECT * FROM guest WHERE passport_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, passportNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuest(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостя по паспорту: " + passportNumber, e);
        }
    }
    
    public List<Guest> findGuestsWithActiveBookings() throws DAOException {
        List<Guest> guests = new ArrayList<>();
        String sql = """
            SELECT DISTINCT g.* 
            FROM guest g
            JOIN booking b ON g.id = b.guest_id
            WHERE b.is_active = 1
            ORDER BY g.name
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            return guests;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске гостей с активными бронированиями", e);
        }
    }
}