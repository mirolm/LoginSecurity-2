package com.lenis0012.bukkit.ls.data;

import java.util.Properties;

import org.bukkit.plugin.Plugin;

import org.bukkit.configuration.file.FileConfiguration;
import com.zaxxer.hikari.HikariConfig;

public class MySQL extends SQL {
	public MySQL(FileConfiguration config, Plugin plugin) {
		this.plugin = plugin;

		String host = config.getString("MySQL.host", "localhost");
		String port = String.valueOf(config.getInt("MySQL.port", 3306));
		String database = config.getString("MySQL.database", "");
		String user = config.getString("MySQL.username", "");
		String pass = config.getString("MySQL.password", "");
		String table = config.getString("MySQL.prefix", "") + "users";

		HikariConfig dbcfg = new HikariConfig();
		Properties prop = new Properties();
		
		prop.setProperty("useConfigs", "maxPerformance");
		prop.setProperty("useServerPrepStmts", "true");
		prop.setProperty("prepStmtCacheSize", "250");
        	prop.setProperty("prepStmtCacheSqlLimit", "2048");
		
        	dbcfg.setDriverClassName("com.mysql.jdbc.Driver");
		dbcfg.setJdbcUrl("jdbc:mysql:" + "//" + host + ":" + port + "/" + database);
		dbcfg.setUsername(user);
        	dbcfg.setPassword(pass);

		dbcfg.setMaximumPoolSize(6);
		dbcfg.setDataSourceProperties(prop);

		init(table, dbcfg);
	}
}
