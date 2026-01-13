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
import com.agnezdei.hotelmvc.repository.GenericDAO;

public class BookingRepository extends BaseRepository implements GenericDAO<Booking, Long> {
    
    private final GuestRepository guestRepo;
    private final RoomRepository roomRepo;
    
    public BookingRepository(DatabaseConfig databaseConfig, 
                            GuestRepository guestRepo, 
                            RoomRepository roomRepo) {
        super(databaseConfig);
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
    }
    
    @Override
    public Booking save(Booking booking) throws DAOException {
        String sql = "INSERT INTO booking (guest_id, room_id, check_in_date, check_out_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setLong(1, booking.getGuest().getId());
            stmt.setLong(2, booking.getRoom().getId());
            stmt.setString(3, booking.getCheckInDate().toString());
            stmt.setString(4, booking.getCheckOutDate().toString());
            stmt.setBoolean(5, booking.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Создание бронирования не удалось, ни одна запись не добавлена");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                booking.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Создание бронирования не удалось, ID не получен");
            }
            
            return booking;
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении бронирования", e);
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    @Override
    public Optional<Booking> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM booking WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToBooking(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирования по ID: " + id, e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking ORDER BY check_in_date DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех бронирований", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookings;
    }
    
    @Override
    public void update(Booking booking) throws DAOException {
        String sql = "UPDATE booking SET guest_id = ?, room_id = ?, check_in_date = ?, " +
                     "check_out_date = ?, is_active = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setLong(1, booking.getGuest().getId());
            stmt.setLong(2, booking.getRoom().getId());
            stmt.setString(3, booking.getCheckInDate().toString());
            stmt.setString(4, booking.getCheckOutDate().toString());
            stmt.setBoolean(5, booking.isActive());
            stmt.setLong(6, booking.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Бронирование не найдено для обновления: ID=" + booking.getId());
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении бронирования: " + booking.getId(), e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM booking WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("Бронирование не найдено для удаления: ID=" + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении бронирования: " + id, e);
        } finally {
            closeResources(null, stmt);
        }
    }
    
    public List<Booking> findActiveBookings() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE is_active = TRUE ORDER BY check_in_date";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске активных бронирований", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookings;
    }
    
    public List<Booking> findByGuestId(Long guestId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE guest_id = ? ORDER BY check_in_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, guestId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований гостя: " + guestId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookings;
    }
    
    public List<Booking> findByRoomId(Long roomId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE room_id = ? ORDER BY check_in_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, roomId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований комнаты: " + roomId, e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookings;
    }
    
    public List<Booking> findBookingsByDateRange(LocalDate startDate, LocalDate endDate) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE check_in_date <= ? AND check_out_date >= ? ORDER BY check_in_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, endDate.toString());
            stmt.setString(2, startDate.toString());
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований по диапазону дат", e);
        } finally {
            closeResources(rs, stmt);
        }
        
        return bookings;
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException, DAOException {
        Booking booking = new Booking();
        
        booking.setId(rs.getLong("id"));
        booking.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
        booking.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
        booking.setActive(rs.getBoolean("is_active"));
        
        Long guestId = rs.getLong("guest_id");
        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new DAOException("Гость не найден: ID=" + guestId));
        booking.setGuest(guest);
        
        Long roomId = rs.getLong("room_id");
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new DAOException("Комната не найдена: ID=" + roomId));
        booking.setRoom(room);
        
        return booking;
    }
}