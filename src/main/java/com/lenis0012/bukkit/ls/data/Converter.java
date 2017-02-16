package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Converter {
    private final String name;
    private final LoginSecurity plugin;

    public Converter(String name, LoginSecurity plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public void convert() {
        Logger logger = plugin.getLogger();
		
        SQLite manager;
        Connection conn = null;
        ResultSet result = null;
        LoginData login;

        manager = new SQLite(name, plugin);

        try {
            if (manager.exists(name) && plugin.conf.usemysql) {
                conn = manager.getConn();
                result = manager.getAllUsers(conn);

                while (result.next()) {
                    login = manager.parseData(result);

                    if (!plugin.data.checkUser(login.uuid)) {
                        plugin.data.regUser(login);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to convert from SQLite to MySQL");
        } finally {
            manager.closeQuietly(result);
            manager.closeQuietly(conn);
            manager.close();
        }
    }
}
