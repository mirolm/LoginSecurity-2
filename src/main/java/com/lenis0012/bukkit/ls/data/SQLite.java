package com.lenis0012.bukkit.ls.data;

import java.io.File;

public class SQLite extends SQL {
	public SQLite(File file) {
		super("org.sqlite.JDBC");

		String table = "users";
		String url  = "jdbc:sqlite://" + file.getAbsolutePath();

		initConnection(table, url);
		openConnection();
	}
}
