package com.lenis0012.bukkit.ls.util;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

public class Translation {
    private static final String langname = "lang.yml";
    private final ConcurrentMap<String, String> langs = Maps.newConcurrentMap();

    public Translation(Plugin plugin) {
        File langFile = getpath(plugin);
        if (!langFile.exists()) {
            plugin.saveResource(langname, false);
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(langFile);

        for (String key : conf.getKeys(false)) {
            String message = ChatColor.translateAlternateColorCodes('&', conf.getString(key));
            if (!message.isEmpty()) {
                langs.put(key, message);
            }
        }
    }

    private File getpath(Plugin plugin) {
        return Paths.get(plugin.getDataFolder().toString(), langname).normalize().toFile();
    }

    public String get(String key) {
        return langs.get(key);
    }
}
