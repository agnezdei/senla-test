package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class RoomRepository extends BaseRepository implements GenericDAO<Room, Long> {
    
    public RoomRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public Room save(Room room) throws DAOException {
        String sql = "INSERT INTO room (number, type, status, price, capacity, stars) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType().name());
            stmt.setString(3, room.getStatus().name()); 
            stmt.setDouble(4, room.getPrice());
            stmt.setInt(5, room.getCapacity());
            stmt.setInt(6, room.getStars());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание комнаты не удалось, ни одна запись не добавлена");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                room.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание комнаты не удалось, ID не получен");
            }
            
            return room;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении комнаты: " + room.getNumber(), e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<Room> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM room WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнаты по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Room> findAll() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY number";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех комнат", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    @Override
    public void update(Room room) throws DAOException {
        String sql = "UPDATE room SET number = ?, type = ?, status = ?, " +
                     "price = ?, capacity = ?, stars = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType().name());
            stmt.setString(3, room.getStatus().name());
            stmt.setDouble(4, room.getPrice());
            stmt.setInt(5, room.getCapacity());
            stmt.setInt(6, room.getStars());
            stmt.setLong(7, room.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Комната не найдена для обновления: ID=" + room.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении комнаты: " + room.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM room WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Комната не найдена для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении комнаты: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public Optional<Room> findByNumber(String number) throws DAOException {
        String sql = "SELECT * FROM room WHERE number = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, number);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнаты по номеру: " + number, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public List<Room> findAvailableRooms() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY number";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске доступных комнат", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    public List<Room> findByType(RoomType type) throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE type = ? ORDER BY number";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, type.name());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнат по типу: " + type, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    public List<Room> findByStatus(RoomStatus status) throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = ? ORDER BY number";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.name());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнат по статусу: " + status, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return rooms;
    }
    
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        
        room.setId(rs.getLong("id"));
        room.setNumber(rs.getString("number"));
        room.setPrice(rs.getDouble("price"));
        room.setCapacity(rs.getInt("capacity"));
        room.setStars(rs.getInt("stars"));
        
        String typeStr = rs.getString("type");
        try {
            room.setType(RoomType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            room.setType(RoomType.STANDARD);
        }
        
        String statusStr = rs.getString("status");
        try {
            room.setStatus(RoomStatus.valueOf(statusStr));
        } catch (IllegalArgumentException e) {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
 
        return room;
    }
    
    protected Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}