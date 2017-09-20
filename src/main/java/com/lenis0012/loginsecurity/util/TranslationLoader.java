package com.lenis0012.loginsecurity.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentMap;

public class TranslationLoader {
    private static final String LANG_NAME = "lang.yml";
    private final ConcurrentMap<String, String> langList = Maps.newConcurrentMap();

    public TranslationLoader(Plugin plugin) {
        File langFile = CommonRoutines.getPath(plugin, LANG_NAME);
        if (!langFile.exists()) {
            plugin.saveResource(LANG_NAME, false);
        }

        InputStreamReader reader = new InputStreamReader(plugin.getResource(LANG_NAME), Charsets.UTF_8);
        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);
        config.setDefaults(defaults);

        for (String key : config.getKeys(false)) {
            String message = ChatColor.translateAlternateColorCodes('&', config.getString(key));
            if (!message.isEmpty()) {
                langList.putIfAbsent(key, message);
            }
        }
    }

    public String get(String key) {
        return langList.get(key);
    }
}
