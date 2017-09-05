package com.lenis0012.loginsecurity.thread;

import com.google.common.collect.Maps;
import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class Timeout implements Runnable {
    private final ConcurrentMap<String, TimeoutData> authList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Timeout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> iterator = authList.keySet().iterator();
        long cycle = Common.currentTime(true);

        while (iterator.hasNext()) {
            String uuid = iterator.next();

            TimeoutData current = authList.get(uuid);
            if (!((cycle - current.timeout) >= plugin.config.timeout)) {
                notify(current);
            } else {
                iterator.remove();

                Player player = Common.getPlayer(uuid);
                if (Common.checkPlayer(player)) {
                    player.kickPlayer(plugin.lang.get("timed_out"));
                }
            }
        }
    }

    public void add(String uuid, boolean registered) {
        TimeoutData current = new TimeoutData(uuid, registered);

        notify(current);

        Player player = Common.getPlayer(uuid);
        if (Common.checkPlayer(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
        }

        authList.putIfAbsent(uuid, current);
    }

    public void remove(String uuid) {
        Player player = Common.getPlayer(uuid);
        if (Common.checkPlayer(player)) {
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
        Player player = Common.getPlayer(current.uuid);
        if (Common.checkPlayer(player)) {
            String message = current.registered ? plugin.lang.get("log_msg") : plugin.lang.get("reg_msg");

            player.sendMessage(message);
        }
    }

    class TimeoutData {
        final String uuid;
        final boolean registered;
        final long timeout;

        TimeoutData(String uuid, boolean registered) {
            this.uuid = uuid;
            this.registered = registered;
            this.timeout = Common.currentTime(true);
        }
    }
}
