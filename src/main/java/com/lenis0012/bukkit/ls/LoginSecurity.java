package com.lenis0012.bukkit.ls;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.UUID;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.lenis0012.bukkit.ls.util.LoggingFilter;
import com.lenis0012.bukkit.ls.commands.ChangePassCommand;
import com.lenis0012.bukkit.ls.commands.LoginCommand;
import com.lenis0012.bukkit.ls.commands.RegisterCommand;
import com.lenis0012.bukkit.ls.data.Converter;
import com.lenis0012.bukkit.ls.data.Converter.FileType;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.data.MySQL;
import com.lenis0012.bukkit.ls.data.SQLite;
import com.lenis0012.bukkit.ls.encryption.EncryptionType;

public class LoginSecurity extends JavaPlugin {

	public DataManager data;
	public static LoginSecurity instance;
	public final ConcurrentMap<String, Boolean> authList = Maps.newConcurrentMap();
	public final ConcurrentMap<String, Integer> failList = Maps.newConcurrentMap();
	public boolean required;
	public int timeDelay, countFail, minFail;
	public ThreadManager thread;
	public EncryptionType hasher;
	public final ConcurrentMap<String, CommandExecutor> commandMap = Maps.newConcurrentMap();
	public static int PHP_VERSION;
	public static String encoder;
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	@Override
	public void onEnable() {
		//setup quickcalls
		FileConfiguration config = this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		
		// load translation
		loadLang();

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
		instance = (LoginSecurity) pm.getPlugin("LoginSecurity");
		data = this.getDataManager(config);
		thread = new ThreadManager(this);
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
		thread.startTimeoutTask();
		thread.startMsgTask();
		thread.startLockTask();

		//convert everything
		this.checkConverter();

		//register events
		pm.registerEvents(new LoginListener(this), this);
		this.registerCommands();
	}

	@Override
	public void onDisable() {
-		if (data != null) {
-			data.close();
-		}
		
		if (thread != null) {
			thread.stopMsgTask();
			thread.stopLockTask();
			thread.stopTimeoutTask();
		}
	}

	private DataManager getDataManager(FileConfiguration config) {
		if (config.getBoolean("MySQL.use")) {
			return new MySQL(config);
		} else {
			File file = new File(this.getDataFolder(), "users.db");
			return new SQLite(file);
		}
	}

	private void checkConverter() {
		File file = new File(this.getDataFolder(), "users.db");
		if (file.exists() && data instanceof MySQL) {
			Converter conv = new Converter(FileType.SQLite, file);
			conv.convert();
		}
	}

	public void registerCommands() {
		this.commandMap.clear();
		this.commandMap.put("login", new LoginCommand());
		this.commandMap.put("register", new RegisterCommand());
		this.commandMap.put("changepass", new ChangePassCommand());

		for (Entry<String, CommandExecutor> entry : this.commandMap.entrySet()) {
			String cmd = entry.getKey();
			CommandExecutor ex = entry.getValue();

			this.getCommand(cmd).setExecutor(ex);
		}
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

	public void setFilter() {
		org.apache.logging.log4j.core.Logger logger; 
		
		logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		logger.addFilter(new LoggingFilter());
	}
	
	public void loadLang() {
		Logger logger = this.getLogger();

		File lang = new File(getDataFolder(), "lang.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		
		for(Lang item:Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		
		Lang.setFile(conf);
		LoginSecurity.LANG = conf;
		LoginSecurity.LANG_FILE = lang;
		
		try {
			conf.save(getLangFile());
		} catch(IOException e) {
			logger.log(Level.WARNING, "Failed to save lang.yml.");
			e.printStackTrace();
		}
	}

	public YamlConfiguration getLang() {
		return LANG;
	}

	public File getLangFile() {
		return LANG_FILE;
	}
}
