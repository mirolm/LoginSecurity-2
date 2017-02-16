package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class TimeoutThread implements Runnable {
    private class TimeoutData {
        public final String uuid;
        public final boolean registered;
        public final long timeout;

        public TimeoutData(String uuid, boolean registered) {
            this.uuid = uuid;
            this.registered = registered;
            this.timeout = System.currentTimeMillis() / 1000L;
        }
    }

    private final ConcurrentMap<String, TimeoutData> authList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;
    private long cycle;

    public TimeoutThread(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = authList.keySet().iterator();
        cycle = System.currentTimeMillis() / 1000L;

        while (it.hasNext()) {
            String puuid = it.next();

            TimeoutData current = authList.get(puuid);
            Player player = Bukkit.getPlayer(UUID.fromString(puuid));

            if (!trigger(current)) {
                if (player != null && player.isOnline()) {
                    if (current.registered) {
                        player.sendMessage(plugin.lang.get("log_msg"));
                    } else {
                        player.sendMessage(plugin.lang.get("reg_msg"));
                    }
                }
            } else {
                it.remove();

                if (player != null && player.isOnline()) {
                    player.kickPlayer(plugin.lang.get("timed_out"));
                }
            }
        }
    }

    public void add(String uuid, boolean registered) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline()) {
            if (registered) {
                player.sendMessage(plugin.lang.get("log_msg"));
            } else {
                player.sendMessage(plugin.lang.get("reg_msg"));
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
        }

        if (!authList.containsKey(uuid)) {
            authList.put(uuid, new TimeoutData(uuid, registered));
        }
    }

    public void remove(String uuid) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline()) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            // ensure that player does not drown after logging in
            player.setRemainingAir(player.getMaximumAir());
        }

        if (authList.containsKey(uuid)) {
            authList.remove(uuid);
        }
    }

    public boolean check(String uuid) {
        return authList.containsKey(uuid);
    }

    private boolean trigger(TimeoutData current) {
        if (authList.containsKey(current.uuid)) {
            return (cycle - current.timeout) >= plugin.conf.timeDelay;
        } else {
            return false;
        }
    }

}
