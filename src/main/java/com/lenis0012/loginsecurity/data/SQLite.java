package com.lenis0012.loginsecurity.data;

import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.Common;
import com.zaxxer.hikari.HikariConfig;

class SQLite extends SQL {
    private static final String DB_NAME = "users.db";

    public SQLite(LoginSecurity plugin) {
        super(plugin);

        String path = Common.getPath(plugin, DB_NAME).toString();

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setJdbcUrl("jdbc:sqlite:" + path);

        dbConfig.setMaximumPoolSize(2);

        super.init(plugin.config.table, dbConfig);
    }
}