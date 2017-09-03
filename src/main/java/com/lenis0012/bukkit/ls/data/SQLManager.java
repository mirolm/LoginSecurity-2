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
     * Update player data
     *
     * @param login LoginData
     */
    void modifyLogin(LoginData login);

    /**
     * Update player last login
     *
     * @param uuid PlayerUUID
     */
    void updateDate(String uuid);

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
