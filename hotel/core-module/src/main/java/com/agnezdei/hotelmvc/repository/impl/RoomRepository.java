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

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, room.getNumber());
                stmt.setString(2, room.getType().name());
                stmt.setString(3, room.getStatus().name());
                stmt.setDouble(4, room.getPrice());
                stmt.setInt(5, room.getCapacity());
                stmt.setInt(6, room.getStars());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DAOException("Создание комнаты не удалось, ни одна запись не добавлена");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        room.setId(generatedKeys.getLong(1));
                    } else {
                        conn.rollback();
                        throw new DAOException("Создание комнаты не удалось, ID не получен");
                    }
                }

                conn.commit();
                return room;

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при сохранении комнаты: " + room.getNumber(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении комнаты: " + room.getNumber(), e);
        }
    }

    @Override
    public void update(Room room) throws DAOException {
        String sql = "UPDATE room SET number = ?, type = ?, status = ?, " +
                "price = ?, capacity = ?, stars = ? WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, room.getNumber());
                stmt.setString(2, room.getType().name());
                stmt.setString(3, room.getStatus().name());
                stmt.setDouble(4, room.getPrice());
                stmt.setInt(5, room.getCapacity());
                stmt.setInt(6, room.getStars());
                stmt.setLong(7, room.getId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    throw new DAOException("Комната не найдена для обновления: ID=" + room.getId());
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при обновлении комнаты: " + room.getId(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении комнаты: " + room.getId(), e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM room WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    conn.rollback();
                    throw new DAOException("Комната не найдена для удаления: ID=" + id);
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при удалении комнаты: " + id, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении комнаты: " + id, e);
        }
    }

    @Override
    public Optional<Room> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM room WHERE id = ?";

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
        String sql = "SELECT * FROM room ORDER BY number";

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

    public Optional<Room> findByNumber(String number) throws DAOException {
        String sql = "SELECT * FROM room WHERE number = ?";

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
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY number";

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

    public List<Room> findAllOrderedByPrice() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY price";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по цене", e);
        }

        return rooms;
    }

    public List<Room> findAllOrderedByCapacity() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY capacity";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по вместимости", e);
        }

        return rooms;
    }

    public List<Room> findAllOrderedByStars() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY stars";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по звездам", e);
        }

        return rooms;
    }

    public List<Room> findAvailableRoomsOrderedByPrice() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY price";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по цене", e);
        }

        return rooms;
    }

    public List<Room> findAvailableRoomsOrderedByCapacity() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY capacity";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по вместимости", e);
        }

        return rooms;
    }

    public List<Room> findAvailableRoomsOrderedByStars() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY stars";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении доступных комнат, отсортированных по звездам", e);
        }

        return rooms;
    }

    public List<Room> findAllOrderedByTypeAndPrice() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY type, price";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении комнат, отсортированных по типу и цене", e);
        }

        return rooms;
    }

    public List<Room> findRoomsAvailableOnDate(LocalDate date) throws DAOException {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT r.* FROM room r WHERE r.status = 'AVAILABLE' " +
                "AND r.id NOT IN (" +
                "  SELECT b.room_id FROM booking b " +
                "  WHERE b.is_active = TRUE " +
                "  AND ? BETWEEN b.check_in_date AND b.check_out_date" +
                ") ORDER BY r.number";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(mapResultSetToRoom(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске комнат на дату: " + date, e);
        }

        return availableRooms;
    }

    public int countAvailableRooms() throws DAOException {
        String sql = "SELECT COUNT(*) as count FROM room WHERE status = 'AVAILABLE'";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;

        } catch (SQLException e) {
            throw new DAOException("Ошибка при подсчете доступных комнат", e);
        }
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

        return room;
    }
}