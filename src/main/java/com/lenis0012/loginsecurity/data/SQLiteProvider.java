package com.lenis0012.loginsecurity.data;

import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.CommonRoutines;
import com.zaxxer.hikari.HikariConfig;

class SQLiteProvider extends SQLProvider {
    SQLiteProvider(LoginSecurity plugin) {
        super(plugin);

        String path = CommonRoutines.getPath(plugin, "users.db").toString();

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setPoolName(plugin.getName());

        dbConfig.setJdbcUrl("jdbc:sqlite:" + path);

        dbConfig.addDataSourceProperty("journal_mode", "WAL");
        dbConfig.addDataSourceProperty("busy_timeout", 5000);
        dbConfig.addDataSourceProperty("synchronous", "NORMAL");
        dbConfig.addDataSourceProperty("temp_store", "MEMORY");
        dbConfig.addDataSourceProperty("cache_size", 8192);
        dbConfig.addDataSourceProperty("mmap_size", 268435456);
        dbConfig.addDataSourceProperty("wal_autocheckpoint", 64);
        dbConfig.addDataSourceProperty("journal_size_limit", 1048576);
        dbConfig.addDataSourceProperty("foreign_keys", "ON");

        dbConfig.setMaximumPoolSize(3);

        super.init(plugin.config.table, dbConfig);
    }
}
