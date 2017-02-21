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

public class Timeout implements Runnable {
    private final ConcurrentMap<String, TimeoutData> authList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Timeout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = authList.keySet().iterator();
        long cycle = seconds();

        while (it.hasNext()) {
            String puuid = it.next();

            TimeoutData current = authList.get(puuid);
            if (!((cycle - current.timeout) >= plugin.conf.timeDelay)) {
                notify(current);
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
        TimeoutData current = new TimeoutData(uuid, registered);

        notify(current);

        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
        }

        authList.putIfAbsent(uuid, current);
    }

    public void remove(String uuid) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline()) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            // ensure that player does not drown after logging in
            player.setRemainingAir(player.getMaximumAir());
        }

        authList.remove(uuid);
    }

    public boolean check(String uuid) {
        return authList.containsKey(uuid);
    }

    private void notify(TimeoutData current) {
        Player player = Bukkit.getPlayer(UUID.fromString(current.uuid));
        if (player != null && player.isOnline()) {
            String message = current.registered ? plugin.lang.get("log_msg") : plugin.lang.get("reg_msg");

            player.sendMessage(message);
        }
    }

    private long seconds() {
        return System.currentTimeMillis() / 1000L;
    }

    class TimeoutData {
        final String uuid;
        final boolean registered;
        final long timeout;

        TimeoutData(String uuid, boolean registered) {
            this.uuid = uuid;
            this.registered = registered;
            this.timeout = seconds();
        }
    }
}
