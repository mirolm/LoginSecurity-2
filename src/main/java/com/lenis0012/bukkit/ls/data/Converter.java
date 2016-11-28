package com.lenis0012.bukkit.ls.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Converter {
	public static enum FileType {
		SQLite;
	}

	private final FileType type;
	private final File file;

	public Converter(FileType type, File file) {
		this.type = type;
		this.file = file;
	}

	public void convert() {
		LoginSecurity plugin = LoginSecurity.instance;
		if(type == FileType.SQLite && !(plugin.data instanceof SQLite)) {
			SQLite manager = null;

			try {
				manager = new SQLite(file);
				ResultSet result = manager.getAllUsers();

				while(result.next()) {
					LoginData login = manager.parseData(result);

					if(!plugin.data.checkUser(login.uuid)) {
						plugin.data.regUser(login);
					}
				}
			} catch(SQLException e) {
				plugin.log.log(Level.WARN, "Failed to convert from SQLite to MySQL");
			} finally {
				manager.closeConn();
			}
		}
	}
}
