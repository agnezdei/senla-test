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
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setLong(1, guestService.getGuest().getId());
                stmt.setLong(2, guestService.getService().getId());
                stmt.setString(3, guestService.getServiceDate().toString());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DAOException("Создание заказа услуги не удалось");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        guestService.setId(generatedKeys.getLong(1));
                    } else {
                        conn.rollback();
                        throw new DAOException("Создание заказа не удалось, ID не получен");
                    }
                }
                
                conn.commit();
                return guestService;
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при сохранении заказа услуги", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении заказа услуги", e);
        }
    }

    @Override
    public void update(GuestService guestService) throws DAOException {
        String sql = "UPDATE guest_service SET guest_id = ?, service_id = ?, service_date = ? WHERE id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setLong(1, guestService.getGuest().getId());
                stmt.setLong(2, guestService.getService().getId());
                stmt.setString(3, guestService.getServiceDate().toString());
                stmt.setLong(4, guestService.getId());
                
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    throw new DAOException("Заказ услуги не найден для обновления: ID=" + guestService.getId());
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при обновлении заказа услуги: " + guestService.getId(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении заказа услуги: " + guestService.getId(), e);
        }
    }
    
    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM guest_service WHERE id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    conn.rollback();
                    throw new DAOException("Заказ услуги не найден для удаления: ID=" + id);
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при удалении заказа услуги: " + id, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении заказа услуги: " + id, e);
        }
    }
    
    @Override
    public Optional<GuestService> findById(Long id) throws DAOException {
        String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuestService(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказа услуги по ID: " + id, e);
        }
    }
    
    @Override
    public List<GuestService> findAll() throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "ORDER BY gs.service_date DESC, g.name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех заказов услуг", e);
        }
        
        return guestServices;
    }

     public List<GuestService> findByGuestId(Long guestId) throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, s.* FROM guest_service gs " +
                     "JOIN service s ON gs.service_id = s.id " +
                     "WHERE gs.guest_id = ? ORDER BY gs.service_date";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, guestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    guestServices.add(mapResultSetToGuestService(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов услуг гостя: " + guestId, e);
        }
        
        return guestServices;
    }
    
    public List<GuestService> findByServiceId(Long serviceId) throws DAOException {
        List<GuestService> guestServices = new ArrayList<>();
        String sql = "SELECT gs.*, g.* FROM guest_service gs " +
                     "JOIN guest g ON gs.guest_id = g.id " +
                     "WHERE gs.service_id = ? ORDER BY gs.service_date";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, serviceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    guestServices.add(mapResultSetToGuestService(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске заказов для услуги : " + serviceId, e);
        }
        
        return guestServices;
    }

    public List<GuestService> findByGuestIdOrderedByPrice(Long guestId) throws DAOException {
    List<GuestService> guestServices = new ArrayList<>();
    String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                 "JOIN guest g ON gs.guest_id = g.id " +
                 "JOIN service s ON gs.service_id = s.id " +
                 "WHERE gs.guest_id = ? ORDER BY s.price";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, guestId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
        }
        
    } catch (SQLException e) {
        throw new DAOException("Ошибка при поиске услуг гостя, отсортированных по цене: " + guestId, e);
    }
    
    return guestServices;
}

public List<GuestService> findByGuestIdOrderedByDate(Long guestId) throws DAOException {
    List<GuestService> guestServices = new ArrayList<>();
    String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                 "JOIN guest g ON gs.guest_id = g.id " +
                 "JOIN service s ON gs.service_id = s.id " +
                 "WHERE gs.guest_id = ? ORDER BY gs.service_date";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, guestId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
        }
        
    } catch (SQLException e) {
        throw new DAOException("Ошибка при поиске услуг гостя, отсортированных по дате: " + guestId, e);
    }
    
    return guestServices;
}

public List<GuestService> findByGuestNameOrderedByPrice(String guestName) throws DAOException {
    List<GuestService> guestServices = new ArrayList<>();
    String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                 "JOIN guest g ON gs.guest_id = g.id " +
                 "JOIN service s ON gs.service_id = s.id " +
                 "WHERE g.name LIKE ? ORDER BY s.price";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, "%" + guestName + "%");
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
        }
        
    } catch (SQLException e) {
        throw new DAOException("Ошибка при поиске услуг гостя по имени, отсортированных по цене: " + guestName, e);
    }
    
    return guestServices;
}

public List<GuestService> findByGuestNameOrderedByDate(String guestName) throws DAOException {
    List<GuestService> guestServices = new ArrayList<>();
    String sql = "SELECT gs.*, g.*, s.* FROM guest_service gs " +
                 "JOIN guest g ON gs.guest_id = g.id " +
                 "JOIN service s ON gs.service_id = s.id " +
                 "WHERE g.name LIKE ? ORDER BY gs.service_date";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, "%" + guestName + "%");
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guestServices.add(mapResultSetToGuestService(rs));
            }
        }
        
    } catch (SQLException e) {
        throw new DAOException("Ошибка при поиске услуг гостя по имени, отсортированных по дате: " + guestName, e);
    }
    
    return guestServices;
    }
    
    private GuestService mapResultSetToGuestService(ResultSet rs) throws SQLException {
        GuestService guestService = new GuestService();
        
        guestService.setId(rs.getLong("gs.id"));
        guestService.setServiceDate(LocalDate.parse(rs.getString("gs.service_date")));
        
        Guest guest = new Guest();
        guest.setId(rs.getLong("g.id"));
        guest.setName(rs.getString("g.name"));
        guest.setPassportNumber(rs.getString("g.passport_number"));
        guestService.setGuest(guest);
        
        Service service = new Service();
        service.setId(rs.getLong("s.id"));
        service.setName(rs.getString("s.name"));
        service.setPrice(rs.getDouble("s.price"));
        
        String categoryStr = rs.getString("s.category");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryStr));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }
        
        guestService.setService(service);
        
        return guestService;
    }
}