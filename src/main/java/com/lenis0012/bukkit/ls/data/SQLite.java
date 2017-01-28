package com.lenis0012.bukkit.ls.data;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class SQLite extends SQL {
	public SQLite(String name) {
		String table = "users";
		String path = getFilePath(name);
		
		HikariConfig dbcfg = new HikariConfig();
		
        	dbcfg.setDriverClassName("org.sqlite.JDBC");
        	dbcfg.setJdbcUrl("jdbc:sqlite:" + path);
	        dbcfg.setUsername("");
        	dbcfg.setPassword("");

		dbcfg.setMaximumPoolSize(1);

		init(table, dbcfg);
	}
	
	private static String getFilePath(String name) {
		LoginSecurity plugin = LoginSecurity.instance;

		return new File(plugin.getDataFolder(), name)
			.toPath().normalize()
			.toString();
	}
}
