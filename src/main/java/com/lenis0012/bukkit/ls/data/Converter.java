package com.lenis0012.bukkit.ls.data;

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
	private final String file;

	public Converter(FileType type, String file) {
		this.type = type;
		this.file = file;
	}

	public void convert() {
		LoginSecurity plugin = LoginSecurity.instance;
		Logger logger = plugin.getLogger();
		
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
				logger.log(Level.WARNING, "Failed to convert from SQLite to MySQL");
			} finally {
				manager.close();
			}
		}
	}
}
