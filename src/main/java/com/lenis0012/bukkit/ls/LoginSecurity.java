package com.lenis0012.bukkit.ls;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.util.LoggingFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lenis0012.bukkit.ls.commands.AdminCommand;
import com.lenis0012.bukkit.ls.commands.ChangePassCommand;
import com.lenis0012.bukkit.ls.commands.LoginCommand;
import com.lenis0012.bukkit.ls.commands.LogoutCommand;
import com.lenis0012.bukkit.ls.commands.RegisterCommand;
import com.lenis0012.bukkit.ls.commands.RmPassCommand;
import com.lenis0012.bukkit.ls.data.Converter;
import com.lenis0012.bukkit.ls.data.Converter.FileType;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.MySQL;
import com.lenis0012.bukkit.ls.data.SQLite;
import com.lenis0012.bukkit.ls.encryption.EncryptionType;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LoginSecurity extends JavaPlugin {

	public DataManager data;
	public static LoginSecurity instance;
	public Map<String, Boolean> authList = Maps.newConcurrentMap();
	public Map<String, Location> loginLocations = Maps.newConcurrentMap();
	public Map<String, Integer> failLogins = Maps.newConcurrentMap();
	public boolean required, blindness, sesUse, timeUse, spawntp;
	public int sesDelay, timeDelay, numLogin;
	public static final Logger log = Logger.getLogger("Minecraft");
	public ThreadManager thread;
	public String prefix;
	public EncryptionType hasher;
	public Map<String, CommandExecutor> commandMap = Maps.newHashMap();
	public static int PHP_VERSION;
	public static String encoder;
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	@Override
	public void onEnable() {
		//setup quickcalls
		FileConfiguration config = this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		loadLang();

		//setup config
		config.addDefault("settings.password-required", true);
		config.addDefault("settings.encryption", "BCRYPT");
		config.addDefault("settings.encoder", "UTF-8");
		config.addDefault("settings.PHP_VERSION", 4);
		config.addDefault("settings.messager-api", true);
		config.addDefault("settings.blindness", true);
		config.addDefault("settings.failed-logins", 3);
		config.addDefault("settings.fake-location", false);
		config.addDefault("settings.session.use", true);
		config.addDefault("settings.session.timeout (sec)", 60);
		config.addDefault("settings.timeout.use", true);
		config.addDefault("settings.timeout.timeout (sec)", 60);
		config.addDefault("settings.table prefix", "ls_");
		config.addDefault("MySQL.use", false);
		config.addDefault("MySQL.host", "localhost");
		config.addDefault("MySQL.port", 3306);
		config.addDefault("MySQL.database", "LoginSecurity");
		config.addDefault("MySQL.username", "root");
		config.addDefault("MySQL.password", "password");
		config.addDefault("MySQL.prefix", "");
		config.options().copyDefaults(true);
		saveConfig();

		//intalize fields
		instance = (LoginSecurity) pm.getPlugin("LoginSecurity");
		prefix = config.getString("settings.table prefix");
		data = this.getDataManager(config, "users.db");
		thread = new ThreadManager(this);
		required = config.getBoolean("settings.password-required");
		blindness = config.getBoolean("settings.blindness");
		numLogin = config.getInt("settings.failed-logins");
		spawntp = config.getBoolean("settings.fake-location");
		sesUse = config.getBoolean("settings.session.use", true);
		sesDelay = config.getInt("settings.session.timeout (sec)", 60);
		timeUse = config.getBoolean("settings.timeout.use", true);
		timeDelay = config.getInt("settings.timeout.timeout (sec)", 60);
		PHP_VERSION = config.getInt("settings.PHP_VERSION", 4);
		this.hasher = EncryptionType.fromString(config.getString("settings.encryption"));
		String enc = config.getString("settings.encoder");
		if (enc.equalsIgnoreCase("utf-16")) {
			encoder = "UTF-16";
		} else {
			encoder = "UTF-8";
		}

		if (sesUse) {
			thread.startSessionTask();
		}
		if (timeUse) {
			thread.startTimeoutTask();
		}

		// Threads
		thread.startMainTask();
		thread.startMsgTask();

		//convert everything
		this.checkConverter();

		//register events
		pm.registerEvents(new LoginListener(this), this);
		this.registerCommands();

		// Filter logs
		org.apache.logging.log4j.core.Logger consoleLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		consoleLogger.addFilter(new LoggingFilter());
	}

	@Override
	public void onDisable() {
		if (data != null) {
			data.closeConnection();
		}
		if (thread != null) {
			thread.stopMsgTask();
			thread.stopSessionTask();
		}
	}

	private DataManager getDataManager(FileConfiguration config, String fileName) {
		if (config.getBoolean("MySQL.use")) {
			return new MySQL(config, this.getConfig().getString("MySQL.prefix", "") + "users");
		} else {
			return new SQLite(new File(this.getDataFolder(), fileName));
		}
	}

	private void checkConverter() {
		File file;
		file = new File(this.getDataFolder(), "data.db");
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
		this.commandMap.put("rmpass", new RmPassCommand());
		this.commandMap.put("logout", new LogoutCommand());
		this.commandMap.put("lac", new AdminCommand());

		for (Entry<String, CommandExecutor> entry : this.commandMap.entrySet()) {
			String cmd = entry.getKey();
			CommandExecutor ex = entry.getValue();

			this.getCommand(cmd).setExecutor(ex);
		}
	}

	public boolean checkLastIp(Player player) {
		String uuid = player.getUniqueId().toString();
		if (data.isRegistered(uuid)) {
			String lastIp = data.getIp(uuid);
			String currentIp = player.getAddress().getAddress().toString();
			return lastIp.equalsIgnoreCase(currentIp);
		}

		return false;
	}

	public void debilitatePlayer(Player player, String name, boolean logout) {
		if (timeUse) {
			thread.timeout.put(name, timeDelay);
		}
		if (blindness) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
		}
		if (spawntp && !logout) {
			loginLocations.put(name, player.getLocation().clone());
			player.teleport(player.getWorld().getSpawnLocation());
		}
		
		// init failed counter
		failLogins.put(name, 0);
	}

	public void rehabPlayer(Player player, String name) {
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		if (spawntp) {
			if (loginLocations.containsKey(name)) {
				Location fixedLocation = loginLocations.remove(name);
				fixedLocation.add(0, 0.2, 0); // fix for players falling into ground
				player.teleport(fixedLocation);
			}
		}
		// ensure that player does not drown after logging in
		player.setRemainingAir(player.getMaximumAir());
		
		// clear failed counter
		failLogins.remove(name);
	}

	public void loadLang() {
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
			log.log(Level.WARNING, "Failed to save lang.yml.");
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
