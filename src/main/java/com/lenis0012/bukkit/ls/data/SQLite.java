package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Common;
import com.zaxxer.hikari.HikariConfig;

public class SQLite extends SQL {
    private static final String dbname = "users.db";

    public SQLite(LoginSecurity plugin) {
        super(plugin);

        String path = Common.getpath(plugin, dbname).toString();

        HikariConfig dbcfg = new HikariConfig();

        dbcfg.setDriverClassName("org.sqlite.JDBC");
        dbcfg.setJdbcUrl("jdbc:sqlite:" + path);
        dbcfg.setUsername("");
        dbcfg.setPassword("");

        dbcfg.setMaximumPoolSize(1);

        super.init(plugin.conf.table, dbcfg);
    }

    public static boolean exists(LoginSecurity plugin) {
        return Common.getpath(plugin, dbname).exists();
    }
}
