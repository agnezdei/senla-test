package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.util.TransactionManager;

public abstract class BaseRepository {
    protected final DatabaseConfig databaseConfig;
    
    public BaseRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    protected Connection getConnection() throws SQLException {
        if (TransactionManager.isTransactionActive()) {
            Connection conn = TransactionManager.getConnection();
            if (conn != null && !conn.isClosed()) {
                return conn;
            }
        }
        
        return databaseConfig.getConnection();
    }
    
    protected void closeResources(ResultSet rs, Statement stmt) {
        closeResources(rs, stmt, null);
    }
    
    protected void closeResources(ResultSet rs, Statement stmt, Connection conn) {
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
                System.err.println("Ошибка при закрытии Statement: " + e.getMessage());
            }
        }
        
        if (conn != null && !TransactionManager.isTransactionActive()) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии Connection: " + e.getMessage());
            }
        }
    }
}