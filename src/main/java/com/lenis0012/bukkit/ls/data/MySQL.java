package com.lenis0012.bukkit.ls.data;

import org.bukkit.configuration.file.FileConfiguration;
import com.zaxxer.hikari.HikariConfig;

public class MySQL extends SQL {
	public MySQL(FileConfiguration config) {
		String host = config.getString("MySQL.host", "localhost");
		String port = String.valueOf(config.getInt("MySQL.port", 3306));
		String database = config.getString("MySQL.database", "");
		String user = config.getString("MySQL.username", "");
		String pass = config.getString("MySQL.password", "");
		String table = config.getString("MySQL.prefix", "") + "users";

		HikariConfig dbConfig = new HikariConfig();
        	
        	dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
		dbConfig.setUsername(user);
        	dbConfig.setPassword(pass);
		dbConfig.setMaximumPoolSize(10);
		dbConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

		initConn(table, dbConfig);
	}
}
