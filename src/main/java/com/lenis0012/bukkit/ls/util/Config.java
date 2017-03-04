package com.lenis0012.bukkit.ls.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public final boolean usemysql;
    public final int timedelay, countfail, minfail;
    public final String hasher;
    public final String host, port, database, user, pass, table;

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
        hasher = config.getString("settings.encryption");
        timedelay = config.getInt("settings.timeout");
        countfail = config.getInt("settings.failed.count");
        minfail = config.getInt("settings.failed.minutes");

        usemysql = config.getBoolean("MySQL.use");
        host = config.getString("MySQL.host");
        port = config.getString("MySQL.port");
        database = config.getString("MySQL.database");
        user = config.getString("MySQL.username");
        pass = config.getString("MySQL.password");

        prefix = config.getString("MySQL.prefix");
        table = prefix.isEmpty() ? "users" : String.format("%s_users", prefix);
    }
}
