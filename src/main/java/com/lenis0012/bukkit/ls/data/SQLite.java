package com.lenis0012.bukkit.ls.data;

import java.io.File;

public class SQLite extends SQL {
	public SQLite(File file) {
		super("org.sqlite.JDBC");

		final String table = "users";

		final String url  = "jdbc:sqlite://" + file.getAbsolutePath();

		initConn(table, url);
	}
}
