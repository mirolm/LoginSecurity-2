package com.lenis0012.bukkit.ls.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Common {
    public static long currentTime(boolean getSeconds) {
        long current = System.currentTimeMillis();

        return getSeconds ? TimeUnit.MILLISECONDS.toSeconds(current) : TimeUnit.MILLISECONDS.toMinutes(current);
    }

    static boolean messageContains(String message, List<String> list) {
        if (message != null && message.length() > 0) {
            String lowerMsg = message.toLowerCase();
            for (String word : list) {
                if (lowerMsg.contains(word)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static File getPath(Plugin plugin, String filename) {
        return Paths.get(plugin.getDataFolder().toString(), filename).normalize().toFile();
    }

    public static boolean checkPlayer(Player player) {
        if (player != null) {
            if (player.isOnline() && !player.hasMetadata("NPC")) {
                return true;
            }
        }

        return false;
    }

    public static Player getPlayer(String uuid) {
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }

    static String getUuid(Player player) {
        return player.getUniqueId().toString();
    }

    static String getUuid(AsyncPlayerPreLoginEvent event) {
        return event.getUniqueId().toString();
    }

    private static String fullUuid(String uuid, String address) {
        return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + address + "#|").getBytes()).toString();
    }

    static String fullUuid(Player player) {
        String uuid = player.getUniqueId().toString();
        String address = player.getAddress().getAddress().getHostAddress();

        return fullUuid(uuid, address);
    }

    static String fullUuid(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        String address = event.getAddress().getHostAddress();

        return fullUuid(uuid, address);
    }


    static boolean invalidName(String name) {
        // 3-16 characters long, letters and numbers
        return !name.matches("^\\w{3,16}$");
    }

    static boolean invalidPassword(String password) {
        // 6-64 characters long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%&*])).{6,64}+$");
    }
}
