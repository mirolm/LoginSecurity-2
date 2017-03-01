package com.lenis0012.bukkit.ls.util;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

public class Translation {
    private static final String langname = "lang.yml";
    private final ConcurrentMap<String, String> langs = Maps.newConcurrentMap();

    public Translation(Plugin plugin) {
        File langFile = Common.getpath(plugin, langname);
        if (!langFile.exists()) {
            plugin.saveResource(langname, false);
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(langFile);

        for (String key : conf.getKeys(false)) {
            String message = ChatColor.translateAlternateColorCodes('&', conf.getString(key));
            if (!message.isEmpty()) {
                langs.putIfAbsent(key, message);
            }
        }
    }

    public String get(String key) {
        return langs.get(key);
    }
}
