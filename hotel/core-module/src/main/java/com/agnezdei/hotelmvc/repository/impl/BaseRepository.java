package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.agnezdei.hotelmvc.config.DatabaseConfig;

public abstract class BaseRepository {
    protected final DatabaseConfig databaseConfig;
    
    public BaseRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    protected Connection getConnection() throws SQLException {
        return databaseConfig.getConnection();
    }
    
    protected void closeResources(ResultSet rs, Statement stmt) {
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
    }
}