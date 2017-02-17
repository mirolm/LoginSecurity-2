package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.zaxxer.hikari.HikariConfig;

import java.io.File;

public class SQLite extends SQL {
    private static final String dbname = "users.db";

    public SQLite(LoginSecurity plugin) {
        this.plugin = plugin;

        String path = getpath();

        HikariConfig dbcfg = new HikariConfig();

        dbcfg.setDriverClassName("org.sqlite.JDBC");
        dbcfg.setJdbcUrl("jdbc:sqlite:" + path);
        dbcfg.setUsername("");
        dbcfg.setPassword("");

        dbcfg.setMaximumPoolSize(1);

        init(plugin.conf.table, dbcfg);
    }

    public static boolean exists(LoginSecurity plugin) {
        File file = new File(plugin.getDataFolder(), dbname);

        return file.exists();
    }

    private String getpath() {
        return new File(plugin.getDataFolder(), dbname)
                .toPath().normalize()
                .toString();
    }
}
