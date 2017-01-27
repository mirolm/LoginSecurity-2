package com.lenis0012.bukkit.ls.data;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;

public class SQLite extends SQL {
	private String JDBC_DRIVER = "org.sqlite.JDBC";

	public SQLite(File file) {
		super(JDBC_DRIVER);

		String table = "users";
        
		HikariConfig dbConfig = new HikariConfig();
		
        	dbConfig.setDriverClassName(JDBC_DRIVER);
	        dbConfig.setUsername("");
        	dbConfig.setPassword("");
            	dbConfig.setMaximumPoolSize(1);
        	dbConfig.setJdbcUrl("jdbc:sqlite://" + file.getAbsolutePath());

		initConn(table, dbConfig);
	}
}
