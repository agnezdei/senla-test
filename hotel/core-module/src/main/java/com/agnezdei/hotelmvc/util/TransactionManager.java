package com.agnezdei.hotelmvc.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.agnezdei.hotelmvc.config.DatabaseConfig;

public class TransactionManager {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> transactionActive = ThreadLocal.withInitial(() -> false);
    
    public static void beginTransaction(DatabaseConfig dbConfig) throws SQLException {
        if (transactionActive.get()) {
            throw new SQLException("Транзакция уже активна");
        }
        
        Connection conn = dbConfig.getConnection();
        conn.setAutoCommit(false);
        connectionHolder.set(conn);
        transactionActive.set(true);
    }
    
    public static Connection getConnection() {
        return connectionHolder.get();
    }
    
    public static void commit() throws SQLException {
        if (!transactionActive.get()) {
            throw new SQLException("Нет активной транзакции");
        }
        
        Connection conn = connectionHolder.get();
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
        }
        
        cleanup();
    }
    
    public static void rollback() {
        try {
            Connection conn = connectionHolder.get();
            if (conn != null && transactionActive.get()) {
                conn.rollback();
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при откате транзакции: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    public static boolean isTransactionActive() {
        return transactionActive.get();
    }
    
    private static void cleanup() {
        connectionHolder.remove();
        transactionActive.set(false);
    }
}