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
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.dao.GenericDAO;

public class ServiceRepository extends BaseRepository implements GenericDAO<Service, Long> {

    public ServiceRepository(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    @Override
    public Service save(Service service) throws DAOException {
        String sql = "INSERT INTO service (name, price, category) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, service.getName());
                stmt.setDouble(2, service.getPrice());
                stmt.setString(3, service.getCategory().name());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DAOException("Создание услуги не удалось, ни одна запись не добавлена");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        service.setId(generatedKeys.getLong(1));
                    } else {
                        conn.rollback();
                        throw new DAOException("Создание услуги не удалось, ID не получен");
                    }
                }
                conn.commit();
                return service;

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при сохранении услуги: " + service.getName(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при сохранении услуги: " + service.getName(), e);
        }
    }

    @Override
    public void update(Service service) throws DAOException {
        String sql = "UPDATE service SET name = ?, price = ?, category = ? WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, service.getName());
                stmt.setDouble(2, service.getPrice());
                stmt.setString(3, service.getCategory().name());
                stmt.setLong(4, service.getId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    throw new DAOException("Услуга не найдена для обновления: ID=" + service.getId());
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при обновлении услуги: " + service.getId(), e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при обновлении услуги: " + service.getId(), e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        String sql = "DELETE FROM service WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    conn.rollback();
                    throw new DAOException("Сервис не найден для удаления: ID=" + id);
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new DAOException("Ошибка при удалении сервиса: " + id, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Ошибка при удалении сервиса: " + id, e);
        }
    }

    @Override
    public Optional<Service> findById(Long id) throws DAOException {
        String sql = "SELECT * FROM service WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToService(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по ID: " + id, e);
        }
    }

    @Override
    public List<Service> findAll() throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service ORDER BY name";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении всех услуг", e);
        }

        return services;
    }

    public Optional<Service> findByName(String name) throws DAOException {
        String sql = "SELECT * FROM service WHERE name = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToService(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуги по названию: " + name, e);
        }
    }

    public List<Service> findByCategory(ServiceCategory category) throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE category = ? ORDER BY name";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при поиске услуг по категории: " + category, e);
        }

        return services;
    }

    public List<Service> findAllOrderedByCategoryAndPrice() throws DAOException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service ORDER BY category, price";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Ошибка при получении услуг, отсортированных по категории и цене", e);
        }

        return services;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();

        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setPrice(rs.getDouble("price"));

        String categoryStr = rs.getString("category");
        try {
            service.setCategory(ServiceCategory.valueOf(categoryStr));
        } catch (IllegalArgumentException e) {
            service.setCategory(ServiceCategory.COMFORT);
        }

        return service;
    }
}