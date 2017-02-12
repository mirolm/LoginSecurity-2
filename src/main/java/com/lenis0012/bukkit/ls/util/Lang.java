package com.lenis0012.bukkit.ls.util;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

public class Lang {
    private final ConcurrentMap<String, String> langs;

    public Lang(LoginSecurity plugin) {
        this.langs = Maps.newConcurrentMap();

        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(langFile);

        for (String key : conf.getKeys(false)) {
            String message = ChatColor.translateAlternateColorCodes('&', conf.getString(key));
            if (!message.isEmpty()) {
                langs.put(key, message);
            }
        }
    }

    public String get (String key) {
        return langs.get(key);
    }
}
