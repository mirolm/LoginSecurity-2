package com.lenis0012.bukkit.ls.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public final boolean usemysql;
    public final int timeDelay;
    public final int countFail;
    public final int minFail;
    public final String hasher;
    public final String host;
    public final String port;
    public final String database;
    public final String user;
    public final String pass;
    public final String table;

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
        timeDelay = config.getInt("settings.timeout", 60);
        countFail = config.getInt("settings.failed.count", 3);
        minFail = config.getInt("settings.failed.minutes", 120);
        hasher = config.getString("settings.encryption");

        usemysql = config.getBoolean("MySQL.use");
        host = config.getString("MySQL.host", "localhost");
        port = String.valueOf(config.getInt("MySQL.port", 3306));
        database = config.getString("MySQL.database", "");
        user = config.getString("MySQL.username", "");
        pass = config.getString("MySQL.password", "");
        table = config.getString("MySQL.prefix", "") + "users";
    }
}
