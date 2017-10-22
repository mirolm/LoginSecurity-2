package com.lenis0012.loginsecurity.data;

import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.CommonRoutines;
import com.zaxxer.hikari.HikariConfig;

class SQLiteProvider extends SQLProvider {
    SQLiteProvider(LoginSecurity plugin) {
        super(plugin);

        String path = CommonRoutines.getPath(plugin, "users.db").toString();

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setJdbcUrl("jdbc:sqlite:" + path);

        dbConfig.setMaximumPoolSize(1);

        super.init(plugin.config.table, dbConfig);
    }
}
