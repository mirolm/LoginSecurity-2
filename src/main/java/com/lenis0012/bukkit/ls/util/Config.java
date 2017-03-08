package com.lenis0012.bukkit.ls.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public final boolean useMysql;
    public final int timeout, failedCount, failedMinutes;
    public final String encryption;
    public final String host, port, database, username, password, table;

    public Config(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String prefix;

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

        useMysql = config.getBoolean("MySQL.use");
        host = config.getString("MySQL.host");
        port = config.getString("MySQL.port");
        database = config.getString("MySQL.database");
        username = config.getString("MySQL.username");
        password = config.getString("MySQL.password");

        prefix = config.getString("MySQL.prefix");
        table = prefix.isEmpty() ? "users" : String.format("%s_users", prefix);
    }
}
