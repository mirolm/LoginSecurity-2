package com.lenis0012.bukkit.ls.data;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;

public class SQLite extends SQL {
	public SQLite(File file) {
		String table = "users";
        
		HikariConfig dbConfig = new HikariConfig();
		
        	dbConfig.setDriverClassName("org.sqlite.JDBC");
        	dbConfig.setJdbcUrl("jdbc:sqlite://" + file.getAbsolutePath());
	        dbConfig.setUsername("");
        	dbConfig.setPassword("");

		dbConfig.setMaximumPoolSize(1);

		initConn(table, dbConfig);
	}
}
