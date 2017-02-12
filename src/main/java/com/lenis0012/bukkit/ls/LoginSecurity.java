package com.lenis0012.bukkit.ls;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.commands.ChangePassCommand;
import com.lenis0012.bukkit.ls.commands.LoginCommand;
import com.lenis0012.bukkit.ls.commands.RegisterCommand;
import com.lenis0012.bukkit.ls.data.Converter;
import com.lenis0012.bukkit.ls.data.Converter.FileType;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.MySQL;
import com.lenis0012.bukkit.ls.data.SQLite;
import com.lenis0012.bukkit.ls.encryption.EncryptionType;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;
import com.lenis0012.bukkit.ls.util.Lang;
import com.lenis0012.bukkit.ls.util.LoggingFilter;
import org.apache.logging.log4j.LogManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class LoginSecurity extends JavaPlugin {
	public DataManager data;
	public PasswordManager passmgr;
	public Lang lang;
	public final ConcurrentMap<String, Boolean> authList = Maps.newConcurrentMap();
	public final ConcurrentMap<String, Integer> failList = Maps.newConcurrentMap();
	public boolean required;
	public int timeDelay, countFail, minFail;
	public ThreadManager thread;
	public EncryptionType hasher;
	public static int PHP_VERSION;
	public static String encoder;

	@Override
	public void onEnable() {
		//setup quickcalls
		FileConfiguration config = this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();

		//filter logs
		setFilter();

		//setup config
		config.addDefault("settings.password-required", true);
		config.addDefault("settings.encryption", "BCRYPT");
		config.addDefault("settings.encoder", "UTF-8");
		config.addDefault("settings.PHP_VERSION", 4);
		config.addDefault("settings.timeout", 60);
		config.addDefault("settings.failed.count", 3);
		config.addDefault("settings.failed.minutes", 120);
		config.addDefault("MySQL.use", false);
		config.addDefault("MySQL.host", "localhost");
		config.addDefault("MySQL.port", 3306);
		config.addDefault("MySQL.database", "");
		config.addDefault("MySQL.username", "");
		config.addDefault("MySQL.password", "");
		config.addDefault("MySQL.prefix", "");
		config.options().copyDefaults(true);
		saveConfig();

		//intalize fields
		passmgr = new PasswordManager(this);
		data = this.getDataManager(config);
		thread = new ThreadManager(this);
		lang = new Lang(this);

		required = config.getBoolean("settings.password-required");
		timeDelay = config.getInt("settings.timeout", 60);
		countFail = config.getInt("settings.failed.count", 3);
		minFail = config.getInt("settings.failed.minutes", 120);
		PHP_VERSION = config.getInt("settings.PHP_VERSION", 4);
		this.hasher = EncryptionType.fromString(config.getString("settings.encryption"));
		String enc = config.getString("settings.encoder");
		if (enc.equalsIgnoreCase("utf-16")) {
			encoder = "UTF-16";
		} else {
			encoder = "UTF-8";
		}

		// Threads
		thread.start();

		//convert everything
		this.checkConverter();

		//register events
		pm.registerEvents(new LoginListener(this), this);
		this.registerCommands();
	}

	@Override
	public void onDisable() {
		if (data != null) {
			data.close();
		}
		
		if (thread != null) {
			thread.stop();
		}
	}

	private DataManager getDataManager(FileConfiguration config) {
		if (config.getBoolean("MySQL.use")) {
			return new MySQL(config, this);
		} else {
			return new SQLite("users.db", this);
		}
	}

	private void checkConverter() {
		File file = new File(this.getDataFolder(), "users.db");
 		if (file.exists() && data instanceof MySQL) {
			Converter conv = new Converter(FileType.SQLite, "users.db", this);
			conv.convert();
		}
	}

	private void registerCommands() {
		getCommand("login").setExecutor(new LoginCommand(this));
		getCommand("register").setExecutor(new RegisterCommand(this));
		getCommand("changepass").setExecutor(new ChangePassCommand(this));
	}

	public boolean checkFailed(String uuid) {
		if (failList.containsKey(uuid)) {
			return failList.put(uuid, failList.get(uuid) + 1)  >= countFail;
		} else {
			failList.put(uuid, 1);
		}

		return false;
	}

	public String getFullUUID(String uuid, String addr) {
                return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
	}

	public void debilitatePlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		
		thread.getTimeout().put(uuid, timeDelay);

		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
	}

	public void rehabPlayer(Player player) {
		player.removePotionEffect(PotionEffectType.BLINDNESS);

		// ensure that player does not drown after logging in
		player.setRemainingAir(player.getMaximumAir());
	}

	private void setFilter() {
		org.apache.logging.log4j.core.Logger logger; 
		
		logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		logger.addFilter(new LoggingFilter());
	}
}
