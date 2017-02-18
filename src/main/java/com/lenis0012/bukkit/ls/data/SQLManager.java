package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.ResultSet;

public interface SQLManager {

    /**
     * Close Connection Pool
     */
    void close();

    /**
     * Get Connection from Pool
     */
    Connection getConn();

    /**
     * Check if player is registered
     *
     * @param uuid PlayerUUID
     * @return user registered
     */
    boolean checkUser(String uuid);

    /**
     * Register a user
     *
     * @param login LoginData
     */
    void regUser(LoginData login);

    /**
     * Update player data
     *
     * @param login LoginData
     */
    void updateUser(LoginData login);

    /**
     * Get user stored data
     *
     * @param uuid PlayerUUID
     * @return LoginData
     */
    LoginData getUser(String uuid);

    /**
     * Get all users data
     *
     * @return All registered users
     */
    ResultSet getAllUsers(Connection con);

    /**
     * Parse single user row
     *
     * @param data ResultSet
     * @return LoginData
     */
    LoginData parseData(ResultSet data);

    /**
     * Close Database Object
     */
    void closeQuietly(AutoCloseable closeable);
}
