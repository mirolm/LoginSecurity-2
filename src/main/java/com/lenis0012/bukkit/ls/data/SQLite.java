package com.lenis0012.bukkit.ls.data;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;

public class SQLite extends SQL {
	public SQLite(File file) {
		super("org.sqlite.JDBC");

		String table = "users";
        
		HikariConfig config = new HikariConfig();
		
        	config.setDriverClassName("org.sqlite.JDBC");
	        config.setUsername("");
        	config.setPassword("");
            	config.setMaximumPoolSize(1);
        	config.setJdbcUrl("jdbc:sqlite://" + file.getAbsolutePath());

		initConn(table, config);
	}
}
