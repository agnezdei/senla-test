package com.agnezdei.hotelmvc.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.dao.interfaces.GenericDAO;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;

public class BookingDAO extends BaseDAO implements GenericDAO<Booking, Long> {
    
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
                
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getLong(1));
                    }
                }
                
                saveBookingServices(conn, booking);
                
                conn.commit();
                return booking;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении бронирования", e);
        }
    }
    
    private void saveBookingServices(Connection conn, Booking booking) throws SQLException {
        String sql = "INSERT INTO booking_service (booking_id, service_id, service_date) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Booking.ServiceWithDate serviceWithDate : booking.getServices()) {
                stmt.setLong(1, booking.getId());
                stmt.setLong(2, serviceWithDate.getService().getId());
                stmt.setString(3, serviceWithDate.getDate().toString());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    @Override
    public Optional<Booking> findById(Long id) throws DAOException {
        String sql = """
            SELECT b.*, 
                   g.name as guest_name, g.passport_number,
                   r.number as room_number, r.price as room_price,
                   rt.name as room_type_name,
                   rs.status as room_status_name
            FROM booking b
            JOIN guest g ON b.guest_id = g.id
            JOIN room r ON b.room_id = r.id
            JOIN room_type rt ON r.room_type_id = rt.id
            JOIN room_status rs ON r.room_status_id = rs.id
            WHERE b.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadBookingServices(conn, booking);
                    return Optional.of(booking);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирования по ID: " + id, e);
        }
    }
    
    private void loadBookingServices(Connection conn, Booking booking) throws SQLException, DAOException {
        String sql = """
            SELECT bs.service_date, s.id as service_id, s.name as service_name, 
                   s.price as service_price, sc.name as category_name
            FROM booking_service bs
            JOIN service s ON bs.service_id = s.id
            JOIN service_category sc ON s.category_id = sc.id
            WHERE bs.booking_id = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, booking.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getLong("service_id"));
                    service.setName(rs.getString("service_name"));
                    service.setPrice(rs.getDouble("service_price"));
                    
                    String categoryName = rs.getString("category_name");
                    try {
                        service.setCategory(ServiceCategory.valueOf(categoryName));
                    } catch (IllegalArgumentException e) {
                        service.setCategory(ServiceCategory.COMFORT);
                    }
                    
                    String serviceDateStr = rs.getString("service_date");
                    LocalDate serviceDate = serviceDateStr != null ? 
                        LocalDate.parse(serviceDateStr) : LocalDate.now();
                    
                    booking.addService(service, serviceDate);
                }
            }
        }
    }
    
    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, 
                g.name as guest_name, g.passport_number,
                r.number as room_number, r.price as room_price,
                rt.name as room_type_name,
                rs.status as room_status_name
            FROM booking b
            JOIN guest g ON b.guest_id = g.id
            JOIN room r ON b.room_id = r.id
            JOIN room_type rt ON r.room_type_id = rt.id
            JOIN room_status rs ON r.room_status_id = rs.id
            ORDER BY b.check_in_date DESC
            """;
        
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                loadBookingServices(conn, booking);
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех бронирований", e);
        }
    }
    
    @Override
    public void update(Booking booking) throws DAOException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                String sql = "UPDATE booking SET guest_id = ?, room_id = ?, check_in_date = ?, " +
                           "check_out_date = ?, is_active = ? WHERE id = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, booking.getGuest().getId());
                    stmt.setLong(2, booking.getRoom().getId());
                    stmt.setString(3, booking.getCheckInDate().toString());
                    stmt.setString(4, booking.getCheckOutDate().toString());
                    stmt.setBoolean(5, booking.isActive());
                    stmt.setLong(6, booking.getId());
                    
                    stmt.executeUpdate();
                }
                
                deleteBookingServices(conn, booking.getId());
                saveBookingServices(conn, booking);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении бронирования: " + booking.getId(), e);
        }
    }
    
    private void deleteBookingServices(Connection conn, Long bookingId) throws SQLException {
        String sql = "DELETE FROM booking_service WHERE booking_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookingId);
            stmt.executeUpdate();
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM booking WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении бронирования: " + id, e);
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));

        String checkInDateStr = rs.getString("check_in_date");
        String checkOutDateStr = rs.getString("check_out_date");
        
        booking.setCheckInDate(LocalDate.parse(checkInDateStr));
        booking.setCheckOutDate(LocalDate.parse(checkOutDateStr));
        
        booking.setActive(rs.getBoolean("is_active"));
    
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
        
        String roomTypeName = rs.getString("room_type_name");
        try {
            room.setType(RoomType.valueOf(roomTypeName));
        } catch (IllegalArgumentException e) {
            room.setType(RoomType.STANDARD);
        }
        
        String roomStatusName = rs.getString("room_status_name");
        try {
            room.setStatus(RoomStatus.valueOf(roomStatusName));
        } catch (IllegalArgumentException e) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        
        booking.setRoom(room);
        
        return booking;
    }
    
    public List<Booking> findActiveBookings() throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, 
                   g.name as guest_name, g.passport_number,
                   r.number as room_number, r.price as room_price,
                   rt.name as room_type_name,
                   rs.status as room_status_name
            FROM booking b
            JOIN guest g ON b.guest_id = g.id
            JOIN room r ON b.room_id = r.id
            JOIN room_type rt ON r.room_type_id = rt.id
            JOIN room_status rs ON r.room_status_id = rs.id
            WHERE b.is_active = 1
            ORDER BY b.check_in_date
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                loadBookingServices(conn, booking);
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске активных бронирований", e);
        }
    }
    
    public List<Booking> findBookingsByGuest(Long guestId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, 
                   g.name as guest_name, g.passport_number,
                   r.number as room_number, r.price as room_price,
                   rt.name as room_type_name,
                   rs.status as room_status_name
            FROM booking b
            JOIN guest g ON b.guest_id = g.id
            JOIN room r ON b.room_id = r.id
            JOIN room_type rt ON r.room_type_id = rt.id
            JOIN room_status rs ON r.room_status_id = rs.id
            WHERE b.guest_id = ?
            ORDER BY b.check_in_date DESC
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, guestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadBookingServices(conn, booking);
                    bookings.add(booking);
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований гостя: " + guestId, e);
        }
    }
    
    public List<Booking> findBookingsByRoom(Long roomId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, 
                   g.name as guest_name, g.passport_number,
                   r.number as room_number, r.price as room_price,
                   rt.name as room_type_name,
                   rs.status as room_status_name
            FROM booking b
            JOIN guest g ON b.guest_id = g.id
            JOIN room r ON b.room_id = r.id
            JOIN room_type rt ON r.room_type_id = rt.id
            JOIN room_status rs ON r.room_status_id = rs.id
            WHERE b.room_id = ?
            ORDER BY b.check_in_date DESC
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roomId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadBookingServices(conn, booking);
                    bookings.add(booking);
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске бронирований комнаты: " + roomId, e);
        }
    }
}