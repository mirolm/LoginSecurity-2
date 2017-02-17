package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Config;
import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

public class MySQL extends SQL {
    public MySQL(LoginSecurity plugin) {
        this.plugin = plugin;

        Config conf = plugin.conf;

        HikariConfig dbcfg = new HikariConfig();
        Properties prop = new Properties();

        prop.setProperty("useConfigs", "maxPerformance");
        prop.setProperty("useServerPrepStmts", "true");
        prop.setProperty("prepStmtCacheSize", "250");
        prop.setProperty("prepStmtCacheSqlLimit", "2048");

        dbcfg.setDriverClassName("com.mysql.jdbc.Driver");
        dbcfg.setJdbcUrl("jdbc:mysql:" + "//" + conf.host + ":" + conf.port + "/" + conf.database);
        dbcfg.setUsername(conf.user);
        dbcfg.setPassword(conf.pass);

        dbcfg.setMaximumPoolSize(6);
        dbcfg.setDataSourceProperties(prop);

        init(conf.table, dbcfg);
    }
}
