package com.lenis0012.bukkit.ls.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.EncryptionUtil;

public class Converter {
	public static enum FileType {
		SQLite;
	}
	
	private FileType type;
	private File file;
	private Logger log = Logger.getLogger("Minecraft");
	
	public Converter(FileType type, File file) {
		this.type = type;
		this.file = file;
	}
	
	public void convert() {
		LoginSecurity plugin = LoginSecurity.instance;
		if(type == FileType.SQLite && !(plugin.data instanceof SQLite)) {
			try {
				SQLite manager = new SQLite(file);
				manager.openConnection();
				ResultSet result = manager.getAllUsers();
				while(result.next()) {
					String user = result.getString("username");
					if(!plugin.data.isRegistered(user)) {
						String pass = result.getString("password");
						plugin.data.register(user, pass, 1, RandomStringUtils.randomAscii(25));
					}
				}
				
				manager.closeConnection();
				file.delete();
			} catch(SQLException e) {
				System.out.println("[LoginSecurity] FAILED CONVERTING FROM SQLITE TO MYSQL");
				log.warning("[LoginSecurity] " + e.getMessage());
			}
		}
	}
}
