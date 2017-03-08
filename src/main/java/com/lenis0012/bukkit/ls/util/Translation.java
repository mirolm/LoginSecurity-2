package com.lenis0012.bukkit.ls.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentMap;

public class Translation {
    private static final String LANG_NAME = "lang.yml";
    private final ConcurrentMap<String, String> langList = Maps.newConcurrentMap();

    public Translation(Plugin plugin) {
        File langFile = Common.getPath(plugin, LANG_NAME);
        if (!langFile.exists()) {
            plugin.saveResource(LANG_NAME, false);
        }

        InputStreamReader reader = new InputStreamReader(plugin.getResource(LANG_NAME), Charsets.UTF_8);
        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(langFile);
        conf.setDefaults(defaults);

        for (String key : conf.getKeys(false)) {
            String message = ChatColor.translateAlternateColorCodes('&', conf.getString(key));
            if (!message.isEmpty()) {
                langList.putIfAbsent(key, message);
            }
        }
    }

    public String get(String key) {
        return langList.get(key);
    }
}
