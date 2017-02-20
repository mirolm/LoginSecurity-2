package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.zaxxer.hikari.HikariConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SQLite extends SQL {
    public SQLite(LoginSecurity plugin) {
        super(plugin);

        String path = getpath(plugin).toString();

        HikariConfig dbcfg = new HikariConfig();

        dbcfg.setDriverClassName("org.sqlite.JDBC");
        dbcfg.setJdbcUrl("jdbc:sqlite:" + path);
        dbcfg.setUsername("");
        dbcfg.setPassword("");

        dbcfg.setMaximumPoolSize(1);

        super.init(plugin.conf.table, dbcfg);
    }

    public static boolean exists(LoginSecurity plugin) {
        return Files.exists(getpath(plugin));
    }

    private static Path getpath(LoginSecurity plugin) {
        return Paths.get(plugin.getDataFolder().toString(), "users.db").normalize();
    }
}
