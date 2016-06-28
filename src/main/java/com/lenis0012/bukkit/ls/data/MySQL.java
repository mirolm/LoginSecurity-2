package com.lenis0012.bukkit.ls.data;

import org.bukkit.configuration.file.FileConfiguration;

public class MySQL extends SQL {
	public MySQL(FileConfiguration config) {
		super("com.mysql.jdbc.Driver");

		String host = config.getString("MySQL.host", "localhost");
		String port = String.valueOf(config.getInt("MySQL.port", 3306));
		String database = config.getString("MySQL.database", "bukkit");
		String user = config.getString("MySQL.username", "root");
		String pass = config.getString("MySQL.password", "");
		String table = config.getString("MySQL.prefix", "") + "users";
		String url = "jdbc:mysql://" + host + ':' + port + '/' + database + '?' + "user=" + user + "&password=" + pass;

		initConnection(table, url);
	}
}
