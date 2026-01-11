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
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;

public class RoomDAO extends BaseDAO implements GenericDAO<Room, Long> {
    
    // Кэш для ID типов и статусов
    private Map<String, Integer> roomTypeCache;
    private Map<String, Integer> roomStatusCache;
    
    // Инициализируем кэши при создании DAO
    public RoomDAO() {
        this.roomTypeCache = new HashMap<>();
        this.roomStatusCache = new HashMap<>();
        loadCaches();
    }
    
    private void loadCaches() {
        loadRoomTypesCache();
        loadRoomStatusesCache();
    }
    
    private void loadRoomTypesCache() {
        String sql = "SELECT id, name FROM room_type";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roomTypeCache.put(rs.getString("name"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки кэша типов комнат: " + e.getMessage());
        }
    }
    
    private void loadRoomStatusesCache() {
        String sql = "SELECT id, status FROM room_status";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roomStatusCache.put(rs.getString("status"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки кэша статусов комнат: " + e.getMessage());
        }
    }
    
    @Override
    public Room save(Room room) throws DAOException {
        String sql = "INSERT INTO room (number, room_type_id, room_status_id, price, capacity, stars) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Получаем ID из кэша
            Integer typeId = roomTypeCache.get(room.getType().name());
            Integer statusId = roomStatusCache.get(room.getStatus().name());
            
            if (typeId == null) {
                throw new DAOException("Тип комнаты не найден: " + room.getType());
            }
            if (statusId == null) {
                throw new DAOException("Статус комнаты не найден: " + room.getStatus());
            }
            
            // Устанавливаем параметры
            stmt.setString(1, room.getNumber());
            stmt.setInt(2, typeId);
            stmt.setInt(3, statusId);
            stmt.setDouble(4, room.getPrice());
            stmt.setInt(5, room.getCapacity());
            stmt.setInt(6, room.getStars());
            
            stmt.executeUpdate();
            
            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getLong(1));
                }
            }
            
            return room;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении комнаты: " + room.getNumber(), e);
        }
    }
    
    @Override
    public Optional<Room> findById(Long id) throws DAOException {
        String sql = "SELECT r.*, rt.name as type_name, rs.status as status_name " +
                     "FROM room r " +
                     "JOIN room_type rt ON r.room_type_id = rt.id " +
                     "JOIN room_status rs ON r.room_status_id = rs.id " +
                     "WHERE r.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнаты по ID: " + id, e);
        }
    }
    
    @Override
    public List<Room> findAll() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as type_name, rs.status as status_name " +
                     "FROM room r " +
                     "JOIN room_type rt ON r.room_type_id = rt.id " +
                     "JOIN room_status rs ON r.room_status_id = rs.id";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех комнат", e);
        }
        
        return rooms;
    }
    
    @Override
    public void update(Room room) throws DAOException {
        String sql = "UPDATE room SET number = ?, room_type_id = ?, room_status_id = ?, " +
                     "price = ?, capacity = ?, stars = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            Integer typeId = roomTypeCache.get(room.getType().name());
            Integer statusId = roomStatusCache.get(room.getStatus().name());
            
            if (typeId == null || statusId == null) {
                throw new DAOException("Тип или статус комнаты не найден в кэше");
            }
            
            stmt.setString(1, room.getNumber());
            stmt.setInt(2, typeId);
            stmt.setInt(3, statusId);
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
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM room WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted == 0) {
                throw new DAOException("Комната не найдена для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении комнаты: " + id, e);
        }
    }
    
    public Optional<Room> findByNumber(String number) throws DAOException {
        String sql = "SELECT r.*, rt.name as type_name, rs.status as status_name " +
                     "FROM room r " +
                     "JOIN room_type rt ON r.room_type_id = rt.id " +
                     "JOIN room_status rs ON r.room_status_id = rs.id " +
                     "WHERE r.number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, number);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнаты по номеру: " + number, e);
        }
    }
    
    public List<Room> findAvailableRooms() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as type_name, rs.status as status_name " +
                     "FROM room r " +
                     "JOIN room_type rt ON r.room_type_id = rt.id " +
                     "JOIN room_status rs ON r.room_status_id = rs.id " +
                     "WHERE rs.status = 'AVAILABLE'";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске доступных комнат", e);
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
        
        // Конвертируем строки в enum
        String typeName = rs.getString("type_name");
        try {
            room.setType(RoomType.valueOf(typeName));
        } catch (IllegalArgumentException e) {
            room.setType(RoomType.STANDARD); // значение по умолчанию
        }
        
        String statusName = rs.getString("status_name");
        try {
            room.setStatus(RoomStatus.valueOf(statusName));
        } catch (IllegalArgumentException e) {
            room.setStatus(RoomStatus.AVAILABLE); // значение по умолчанию
        }
        
        return room;
    }
}