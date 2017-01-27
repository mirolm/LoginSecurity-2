package com.lenis0012.bukkit.ls.data;

import java.util.Properties;

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
		Properties properties = new Properties();
		
		properties.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
		properties.setProperty("characterEncoding", "utf8");
        	properties.setProperty("encoding","UTF-8");
        	properties.setProperty("useUnicode", "true");
		
        	properties.setProperty("rewriteBatchedStatements", "true");
        	properties.setProperty("jdbcCompliantTruncation", "false");

        	properties.setProperty("cachePrepStmts", "true");
        	properties.setProperty("prepStmtCacheSize", "275");
        	properties.setProperty("prepStmtCacheSqlLimit", "2048");
		
        	dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
		dbConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		dbConfig.setUsername(user);
        	dbConfig.setPassword(pass);

		dbConfig.setMaximumPoolSize(10);
		dbConfig.setDataSourceProperties(properties);

		initPool(table, dbConfig);
	}
}
