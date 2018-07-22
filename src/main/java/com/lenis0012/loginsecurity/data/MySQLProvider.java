package com.lenis0012.loginsecurity.data;

import com.lenis0012.loginsecurity.LoginSecurity;
import com.zaxxer.hikari.HikariConfig;

class MySQLProvider extends SQLProvider {
    MySQLProvider(LoginSecurity plugin) {
        super(plugin);

        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setPoolName(plugin.getName());

        dbConfig.setJdbcUrl("jdbc:mysql:" + "//" + plugin.config.host + ":" + plugin.config.port + "/" + plugin.config.database);
        dbConfig.setUsername(plugin.config.username);
        dbConfig.setPassword(plugin.config.password);

        dbConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        dbConfig.addDataSourceProperty("maintainTimeStats", "false");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dbConfig.addDataSourceProperty("useConfigs", "maxPerformance");
        dbConfig.addDataSourceProperty("useLocalTransactionState", "true");
        dbConfig.addDataSourceProperty("useServerPrepStmts", "true");

        dbConfig.setMaximumPoolSize(6);

        super.init(plugin.config.table, dbConfig);
    }
}
