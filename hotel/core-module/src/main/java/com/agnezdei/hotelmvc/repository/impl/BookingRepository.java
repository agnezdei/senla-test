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
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class BookingRepository extends BaseRepository implements GenericDAO<Booking, Long> {
    
    public BookingRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }
    
    @Override
    public Booking save(Booking booking) throws DAOException {
        String sql = "INSERT INTO booking (guest_id, room_id, check_in_date, check_out_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setLong(1, booking.getGuest().getId());
                stmt.setLong(2, booking.getRoom().getId());
                stmt.setString(3, booking.getCheckInDate().toString());
                stmt.setString(4, booking.getCheckOutDate().toString());
                stmt.setBoolean(5, booking.isActive());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DAOException("Создание бронирования не удалось, ни одна запись не добавлена");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getLong(1));
                    } else {
                        conn.rollback();
                        throw new DAOException("Создание бронирования не удалось, ID не получен");
                    }
                }
                
                conn.commit();
                return booking;
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при сохранении бронирования", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении бронирования", e);
        }
    }
    
    @Override
    public Optional<Booking> findById(Long id) throws DAOException {
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирования по ID: " + id, e);
        }
    }
    
    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "ORDER BY b.check_in_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех бронирований", e);
        }
        
        return bookings;
    }
    
    @Override
    public void update(Booking booking) throws DAOException {
        String sql = "UPDATE booking SET guest_id = ?, room_id = ?, check_in_date = ?, " +
                     "check_out_date = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setLong(1, booking.getGuest().getId());
                stmt.setLong(2, booking.getRoom().getId());
                stmt.setString(3, booking.getCheckInDate().toString());
                stmt.setString(4, booking.getCheckOutDate().toString());
                stmt.setBoolean(5, booking.isActive());
                stmt.setLong(6, booking.getId());
                
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    throw new DAOException("Бронирование не найдено для обновления: ID=" + booking.getId());
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при обновлении бронирования: " + booking.getId(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении бронирования: " + booking.getId(), e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM booking WHERE id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    conn.rollback();
                    throw new DAOException("Бронирование не найдено для удаления: ID=" + id);
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при удалении бронирования: " + id, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении бронирования: " + id, e);
        }
    }
    
    public List<Booking> findActiveBookings() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.is_active = TRUE ORDER BY b.check_in_date";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске активных бронирований", e);
        }
        
        return bookings;
    }

    public List<Booking> findActiveBookingsOrderedByGuestName() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.is_active = TRUE ORDER BY g.name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске активных бронирований, отсортированных по имени гостя", e);
        }
        
        return bookings;
    }

    public List<Booking> findActiveBookingsOrderedByCheckoutDate() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.is_active = TRUE ORDER BY b.check_out_date";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске активных бронирований, отсортированных по дате выезда", e);
        }
        
        return bookings;
    }
    
    public List<Booking> findByRoomId(Long roomId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.room_id = ? ORDER BY b.check_in_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roomId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований комнаты: " + roomId, e);
        }
        
        return bookings;
    }
    
    public List<Booking> findLastThreeGuestsByRoomId(Long roomId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                     "b.id as booking_id, b.check_in_date, b.check_out_date, b.is_active, " +
                     "g.id as guest_id, g.name as guest_name, g.passport_number, " +
                     "r.id as room_id, r.number as room_number, r.type as room_type, " +
                     "r.status as room_status, r.price as room_price, " +
                     "r.capacity as room_capacity, r.stars as room_stars " +
                     "FROM booking b " +
                     "JOIN guest g ON b.guest_id = g.id " +
                     "JOIN room r ON b.room_id = r.id " +
                     "WHERE b.room_id = ? AND b.is_active = FALSE " +
                     "ORDER BY b.check_out_date DESC LIMIT 3";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roomId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске истории комнаты: " + roomId, e);
        }
        
        return bookings;
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        
        // Используем алиасы из SQL-запроса
        booking.setId(rs.getLong("booking_id"));
        booking.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
        booking.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
        booking.setActive(rs.getBoolean("is_active"));
        
        Guest guest = new Guest();
        guest.setId(rs.getLong("guest_id"));
        guest.setName(rs.getString("guest_name"));
        guest.setPassportNumber(rs.getString("passport_number"));
        booking.setGuest(guest);
        
        Room room = new Room();
        room.setId(rs.getLong("room_id"));
        room.setNumber(rs.getString("room_number"));
        room.setPrice(rs.getDouble("room_price"));
        room.setCapacity(rs.getInt("room_capacity"));
        room.setStars(rs.getInt("room_stars"));
        
        String typeStr = rs.getString("room_type");
        try {
            room.setType(RoomType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            room.setType(RoomType.STANDARD);
        }
        
        String statusStr = rs.getString("room_status");
        try {
            room.setStatus(RoomStatus.valueOf(statusStr));
        } catch (IllegalArgumentException e) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        
        booking.setRoom(room);
        
        return booking;
    }
    
    // Дополнительный метод для отладки: простое получение бронирований без JOIN
    public List<Booking> findAllSimple() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking ORDER BY check_in_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getLong("id"));
                booking.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
                booking.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
                booking.setActive(rs.getBoolean("is_active"));
                
                // Создаем пустые объекты для гостя и комнаты, чтобы избежать NPE
                Guest guest = new Guest();
                guest.setId(rs.getLong("guest_id"));
                booking.setGuest(guest);
                
                Room room = new Room();
                room.setId(rs.getLong("room_id"));
                booking.setRoom(room);
                
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Упрощенный findAllSimple: " + e.getMessage());
            return new ArrayList<>();
        }
        
        return bookings;
    }
}