package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Converter {
	public enum FileType {
		SQLite
	}

	private final FileType type;
	private final String name;
	private final LoginSecurity plugin;

	public Converter(FileType type, String name, LoginSecurity plugin) {
		this.type = type;
		this.name = name;
		this.plugin = plugin;
	}

	public void convert() {
		Logger logger = plugin.getLogger();
		
		if(type == FileType.SQLite && !(plugin.data instanceof SQLite)) {
			SQLite manager;
			Connection conn = null;
			ResultSet result = null;
			LoginData login;

			manager = new SQLite(name, plugin);

			try {
				conn = manager.getConn();
				result = manager.getAllUsers(conn);

				while(result.next()) {
					login = manager.parseData(result);

					if(!plugin.data.checkUser(login.uuid)) {
						plugin.data.regUser(login);
					}
				}
			} catch(Exception e) {
				logger.log(Level.WARNING, "Failed to convert from SQLite to MySQL");
			} finally {
				manager.closeQuietly(result);
				manager.closeQuietly(conn);
				manager.close();
			}
		}
	}
}
