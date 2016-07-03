package com.lenis0012.bukkit.ls.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Converter {
	public static enum FileType {
		SQLite;
	}

	private final FileType type;
	private final File file;
	private final Logger log = Logger.getLogger("Minecraft");

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

					if(!plugin.data.isRegistered(login.uuid)) {
						plugin.data.register(login);
					}
				}
			} catch(SQLException e) {
				log.warning("[LoginSecurity] Failed to convert from SQLite to MySQL");
			} finally {
				manager.closeConnection();
			}
		}
	}
}
