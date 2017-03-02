package com.lenis0012.bukkit.ls.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public final class Common {
    public static long seconds() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String fulluuid(String uuid, String addr) {
        return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
    }

    public static boolean contains(String message, List<String> list) {

        String lowermsg = message.toLowerCase();
        for (String word : list) {
            if (lowermsg.contains(word)) {
                return true;
            }
        }

        return false;
    }

    public static File getpath(Plugin plugin, String filename) {
        return Paths.get(plugin.getDataFolder().toString(), filename).normalize().toFile();
    }

    public static boolean checkplayer(Player player) {
        if (player != null) {
            if (player.isOnline() && !player.hasMetadata("NPC")) {
                return true;
            }
        }

        return false;
    }
}
