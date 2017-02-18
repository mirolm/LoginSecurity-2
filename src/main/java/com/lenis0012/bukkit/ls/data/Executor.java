package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Executor {
    private final LoginSecurity plugin;
    private final Logger logger;
    private final DataManager data;

    public Executor(LoginSecurity plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.data = plugin.conf.usemysql ? new MySQL(plugin) : new SQLite(plugin);

        convert();
    }

    public void disable() {
        data.close();
    }

    public boolean check(String uuid) {
        return data.checkUser(uuid);
    }

    public LoginData get(String uuid) {
        return data.getUser(uuid);
    }

    public void register(LoginData login) {
        if (!data.checkUser(login.uuid)) {
            data.regUser(login);
        }
    }

    public void update(LoginData login) {
        if (data.checkUser(login.uuid)) {
            data.updateUser(login);
        }
    }

    private void convert() {
        DataManager manager;
        Connection conn = null;
        ResultSet result = null;
        LoginData login;

        if (SQLite.exists(plugin) && plugin.conf.usemysql) {
            manager = new SQLite(plugin);

            try {
                conn = manager.getConn();
                result = manager.getAllUsers(conn);

                while (result.next()) {
                    login = manager.parseData(result);

                    register(login);
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
}
