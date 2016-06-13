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
import com.lenis0012.bukkit.ls.util.ReflectionUtil;

public class Converter {
	public static enum FileType {
		YAML,
		SQLite,
		OldToNewMySQL;
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
		if(type == FileType.YAML) {
			boolean md5 = plugin.getConfig().getBoolean("options.use-MD5 Enryption", true);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(config.getConfigurationSection("password.password") != null) {
				Set<String> set = config.getConfigurationSection("password.password").getKeys(false);
				for(String user : set) {
					String pass = config.getString("password.password."+user);
					if(!md5)
						pass = EncryptionUtil.getMD5(pass);
					plugin.data.register(user, pass, 1, RandomStringUtils.randomAscii(25));
				}
			}
			file.delete();
		} else if(type == FileType.SQLite && !(plugin.data instanceof SQLite)) {
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
				log.warning("[LoginSecurity] "+e.getMessage());
			}
		} else if(type == FileType.OldToNewMySQL) {
			try {
				MySQL from = new MySQL(plugin.getConfig(), "passwords");
				from.openConnection();
				boolean shouldEncrypt = plugin.getConfig().getBoolean("options.use-MD5 Enryption", true);
				ResultSet result = from.getAllUsers();
				while(result.next()) {
					String user = result.getString("username");
					if(!plugin.data.isRegistered(user)) {
						String pass = result.getString("password");
						if(!shouldEncrypt)
							pass = EncryptionUtil.getMD5(pass);
						plugin.data.register(user, pass, 1, RandomStringUtils.randomAscii(25));
					}
				}
				from.dropTable("passwords");
				from.closeConnection();
			} catch(SQLException e) {
				System.out.println("[LoginSecurity] FAILED CONVERTING FROM SQLITE TO MYSQL");
				log.warning("[LoginSecurity] "+e.getMessage());
			}
		}
	}
}
