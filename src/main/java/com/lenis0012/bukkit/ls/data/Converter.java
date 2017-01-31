package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Converter {
	public static enum FileType {
		SQLite;
	}

	private final FileType type;
	private final String name;

	public Converter(FileType type, String name) {
		this.type = type;
		this.name = name;
	}

	public void convert() {
		LoginSecurity plugin = LoginSecurity.instance;
		Logger logger = plugin.getLogger();
		
		if(type == FileType.SQLite && !(plugin.data instanceof SQLite)) {
			SQLite manager = null;
			Connection conn = null;
			ResultSet result = null;

			try {
				manager = new SQLite(name);
				conn = manager.getConn();
				result = manager.getAllUsers(conn);

				while(result.next()) {
					LoginData login = manager.parseData(result);

					if(!plugin.data.checkUser(login.uuid)) {
						plugin.data.regUser(login);
					}
				}
				
				conn.commit();
			} catch(SQLException e) {
				conn.rollback();
				logger.log(Level.WARNING, "Failed to convert from SQLite to MySQL");
			} finally {
				manager.closeQuietly(result);
				manager.closeQuietly(conn);
				manager.close();
			}
		}
	}
}
