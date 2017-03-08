package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Config;
import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

class MySQL extends SQL {
    public MySQL(LoginSecurity plugin) {
        super(plugin);

        Config config = plugin.config;

        HikariConfig dbConfig = new HikariConfig();
        Properties prop = new Properties();

        prop.setProperty("useConfigs", "maxPerformance");
        prop.setProperty("useServerPrepStmts", "true");
        prop.setProperty("prepStmtCacheSize", "250");
        prop.setProperty("prepStmtCacheSqlLimit", "2048");

        dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
        dbConfig.setJdbcUrl("jdbc:mysql:" + "//" + config.host + ":" + config.port + "/" + config.database);
        dbConfig.setUsername(config.username);
        dbConfig.setPassword(config.password);

        dbConfig.setMaximumPoolSize(6);
        dbConfig.setDataSourceProperties(prop);

        super.init(config.table, dbConfig);
    }
}
