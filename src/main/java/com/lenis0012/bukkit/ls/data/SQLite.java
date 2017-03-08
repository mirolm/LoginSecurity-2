package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Common;
import com.zaxxer.hikari.HikariConfig;

class SQLite extends SQL {
    private static final String DB_NAME = "users.db";

    public SQLite(LoginSecurity plugin) {
        super(plugin);

        String path = Common.getpath(plugin, DB_NAME).toString();

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setDriverClassName("org.sqlite.JDBC");
        dbConfig.setJdbcUrl("jdbc:sqlite:" + path);

        dbConfig.setMaximumPoolSize(2);

        super.init(plugin.conf.table, dbConfig);
    }

    public static boolean exists(LoginSecurity plugin) {
        return Common.getpath(plugin, DB_NAME).exists();
    }
}
