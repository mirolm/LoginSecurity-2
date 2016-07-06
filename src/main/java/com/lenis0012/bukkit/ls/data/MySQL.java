package com.lenis0012.bukkit.ls.data;

import org.bukkit.configuration.file.FileConfiguration;

public class MySQL extends SQL {
	public MySQL(FileConfiguration config) {
		super("com.mysql.jdbc.Driver");

		final String host = config.getString("MySQL.host", "localhost");
		final String port = String.valueOf(config.getInt("MySQL.port", 3306));
		final String database = config.getString("MySQL.database", "");
		final String user = config.getString("MySQL.username", "");
		final String pass = config.getString("MySQL.password", "");
		final String table = config.getString("MySQL.prefix", "") + "users";

		final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + pass;

		initConn(table, url);
	}
}
