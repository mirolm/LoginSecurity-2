package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Config;
import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

class MySQL extends SQL {
    public MySQL(LoginSecurity plugin) {
        super(plugin);

        Config conf = plugin.conf;

        HikariConfig dbConfig = new HikariConfig();
        Properties prop = new Properties();

        prop.setProperty("useConfigs", "maxPerformance");
        prop.setProperty("useServerPrepStmts", "true");
        prop.setProperty("prepStmtCacheSize", "250");
        prop.setProperty("prepStmtCacheSqlLimit", "2048");

        dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
        dbConfig.setJdbcUrl("jdbc:mysql:" + "//" + conf.host + ":" + conf.port + "/" + conf.database);
        dbConfig.setUsername(conf.user);
        dbConfig.setPassword(conf.pass);

        dbConfig.setMaximumPoolSize(6);
        dbConfig.setDataSourceProperties(prop);

        super.init(conf.table, dbConfig);
    }
}
