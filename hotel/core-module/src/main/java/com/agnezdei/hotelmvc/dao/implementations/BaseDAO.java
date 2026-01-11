package com.agnezdei.hotelmvc.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.exceptions.DAOException;

public abstract class BaseDAO {
    
    // Статический метод для получения соединения
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
    
    // Метод для выполнения операций в транзакции
    protected void executeInTransaction(TransactionalOperation operation) throws DAOException {
        Connection conn = null;
        boolean originalAutoCommit = true;
        
        try {
            conn = getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            operation.execute(conn);
            conn.commit();
            
        } catch (SQLException | DAOException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new DAOException("Ошибка при откате транзакции", rollbackEx);
                }
            }
            throw new DAOException("Ошибка в транзакции", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    // Игнорируем
                }
            }
        }
    }
    
    @FunctionalInterface
    protected interface TransactionalOperation {
        void execute(Connection connection) throws SQLException, DAOException;
    }
    
    // Методы для безопасного закрытия ресурсов (но не Connection!)
    protected void closeResources(ResultSet rs, PreparedStatement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии ResultSet: " + e.getMessage());
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    // Вспомогательный метод для выполнения запросов
    protected ResultSet executeQuery(Connection conn, String sql, Object... params) 
            throws SQLException, DAOException {
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }
}