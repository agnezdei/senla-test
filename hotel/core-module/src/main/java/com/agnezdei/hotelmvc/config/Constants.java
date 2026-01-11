package com.agnezdei.hotelmvc.config;

public class Constants {
    public static final String FIND_ALL_ROOMS = 
        "SELECT r.*, rt.name as type_name, rs.status as status_name " +
        "FROM room r " +
        "JOIN room_type rt ON r.room_type_id = rt.id " +
        "JOIN room_status rs ON r.room_status_id = rs.id";
    
    public static final String FIND_ROOM_BY_ID = FIND_ALL_ROOMS + " WHERE r.id = ?";
    public static final String FIND_ROOM_BY_NUMBER = FIND_ALL_ROOMS + " WHERE r.number = ?";
    
    public static final String INSERT_ROOM = 
        "INSERT INTO room (number, room_type_id, room_status_id, price, capacity, stars) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    
    public static final String UPDATE_ROOM = 
        "UPDATE room SET number = ?, room_type_id = ?, room_status_id = ?, " +
        "price = ?, capacity = ?, stars = ? WHERE id = ?";
    
    public static final String DELETE_ROOM = "DELETE FROM room WHERE id = ?";
    
    public static final String ROOM_STATUS_AVAILABLE = "AVAILABLE";
    public static final String ROOM_STATUS_OCCUPIED = "OCCUPIED";
    public static final String ROOM_STATUS_UNDER_MAINTENANCE = "UNDER_MAINTENANCE";
}