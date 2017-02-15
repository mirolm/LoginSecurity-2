package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class TimeoutThread extends BukkitRunnable {
    private class TimeoutData {
        boolean registered;
        int timeout;

        public TimeoutData(boolean registered, int timeout) {
            this.registered = registered;
            this.timeout = timeout;
        }
    }

    private final ConcurrentMap<String, TimeoutData> authList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public TimeoutThread(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = authList.keySet().iterator();
        while (it.hasNext()) {
            String puuid = it.next();
            TimeoutData current = authList.get(puuid);
            if (current.timeout >= 1) {
                current.timeout -= 1;
                authList.put(puuid, current);

                if (System.currentTimeMillis() / 1000L % 10 == 0) {
                    Player player = Bukkit.getPlayer(UUID.fromString(puuid));

                    if (player != null && player.isOnline()) {
                        if (current.registered) {
                            player.sendMessage(plugin.lang.get("reg_msg"));
                        } else {
                            player.sendMessage(plugin.lang.get("log_msg"));
                        }
                    }
                }
            } else {
                it.remove();

                Player player = Bukkit.getPlayer(UUID.fromString(puuid));
                if (player != null && player.isOnline()) {
                    player.kickPlayer(plugin.lang.get("timed_out"));
                }
            }
        }
    }

    public void add(String uuid, boolean registered) {
        if (!authList.containsKey(uuid)) {
            authList.put(uuid, new TimeoutData(registered, plugin.conf.timeDelay));
        }
    }

    public void remove(String uuid) {
        if (authList.containsKey(uuid)) {
            authList.remove(uuid);
        }
    }

    public boolean check(String uuid) {
        return authList.containsKey(uuid);
    }
}
