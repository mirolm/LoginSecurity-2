package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Config;
import com.zaxxer.hikari.HikariConfig;

class MySQL extends SQL {
    public MySQL(LoginSecurity plugin) {
        super(plugin);

        Config config = plugin.config;

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setJdbcUrl("jdbc:mysql:" + "//" + config.host + ":" + config.port + "/" + config.database);
        dbConfig.setUsername(config.username);
        dbConfig.setPassword(config.password);

        dbConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        dbConfig.addDataSourceProperty("maintainTimeStats", "false");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dbConfig.addDataSourceProperty("useConfigs", "maxPerformance");
        dbConfig.addDataSourceProperty("useLocalTransactionState", "true");
        dbConfig.addDataSourceProperty("useServerPrepStmts", "true");

        dbConfig.setMaximumPoolSize(6);

        super.init(config.table, dbConfig);
    }
}
