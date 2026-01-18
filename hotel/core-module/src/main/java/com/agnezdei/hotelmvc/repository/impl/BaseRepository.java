package com.agnezdei.hotelmvc.repository.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.agnezdei.hotelmvc.config.DatabaseConfig;

public abstract class BaseRepository {
    protected final DatabaseConfig databaseConfig;
    
    public BaseRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    protected Connection getConnection() throws SQLException {
        return databaseConfig.getConnection();
    }
}