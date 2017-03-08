package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;

interface SQLManager {

    /**
     * Close Connection Pool
     */
    void close();

    /**
     * Get Connection from Pool
     */
    Connection getConnection();

    /**
     * Check if player is registered
     *
     * @param uuid PlayerUUID
     * @return user registered
     */
    boolean checkLogin(String uuid);

    /**
     * Register a user
     *
     * @param login LoginData
     */
    void registerLogin(LoginData login);

    /**
     * Update player data
     *
     * @param login LoginData
     */
    void updateLogin(LoginData login);

    /**
     * Get user stored data
     *
     * @param uuid PlayerUUID
     * @return LoginData
     */
    LoginData getLogin(String uuid);

    /**
     * Convert all data
     *
     * @param manager SQLManager
     */
    void convertAllLogin(SQLManager manager);

}
