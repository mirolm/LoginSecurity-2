package com.lenis0012.bukkit.ls.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public final boolean useMySQL;
    public final int timeout;
    public final int failedCount;
    public final int failedMinutes;
    public final String host;
    public final String port;
    public final String database;
    public final String username;
    public final String password;
    public final String table;
    final String encryption;

    public Config(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();

        //setup config
        config.addDefault("settings.encryption", "BCRYPT");
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
        plugin.saveConfig();

        //read values
        encryption = config.getString("settings.encryption");
        timeout = config.getInt("settings.timeout");
        failedCount = config.getInt("settings.failed.count");
        failedMinutes = config.getInt("settings.failed.minutes");

        useMySQL = config.getBoolean("MySQL.use");
        host = config.getString("MySQL.host");
        port = config.getString("MySQL.port");
        database = config.getString("MySQL.database");
        username = config.getString("MySQL.username");
        password = config.getString("MySQL.password");

        String prefix = config.getString("MySQL.prefix");
        table = prefix.isEmpty() ? "users" : String.format("%s_users", prefix);
    }
}
