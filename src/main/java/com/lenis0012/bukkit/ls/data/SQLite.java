package com.lenis0012.bukkit.ls.data;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;

public class SQLite extends SQL {
	public SQLite(File file) {
		String table = "users";
		String path = file.toPath().normalize().toString();
		
		HikariConfig dbcfg = new HikariConfig();
		
        	dbcfg.setDriverClassName("org.sqlite.JDBC");
        	dbcfg.setJdbcUrl("jdbc:sqlite://" + path);
	        dbcfg.setUsername("");
        	dbcfg.setPassword("");

		dbcfg.setMaximumPoolSize(1);

		init(table, dbcfg);
	}
}
