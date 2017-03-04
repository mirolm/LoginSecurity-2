package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class Timeout implements Runnable {
    private final ConcurrentMap<String, TimeoutData> authlist = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Timeout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = authlist.keySet().iterator();
        long cycle = Common.seconds();

        while (it.hasNext()) {
            String puuid = it.next();

            TimeoutData current = authlist.get(puuid);
            if (!((cycle - current.timeout) >= plugin.conf.timedelay)) {
                notify(current);
            } else {
                it.remove();

                Player player = Common.getplayer(puuid);
                if (Common.checkplayer(player)) {
                    player.kickPlayer(plugin.lang.get("timed_out"));
                }
            }
        }
    }

    public void add(String uuid, boolean registered) {
        TimeoutData current = new TimeoutData(uuid, registered);

        notify(current);

        Player player = Common.getplayer(uuid);
        if (Common.checkplayer(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
        }

        authlist.putIfAbsent(uuid, current);
    }

    public void remove(String uuid) {
        Player player = Common.getplayer(uuid);
        if (Common.checkplayer(player)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            // ensure that player does not drown after logging in
            player.setRemainingAir(player.getMaximumAir());
        }

        authlist.remove(uuid);
    }

    public boolean check(String uuid) {
        return authlist.containsKey(uuid);
    }

    private void notify(TimeoutData current) {
        Player player = Common.getplayer(current.uuid);
        if (Common.checkplayer(player)) {
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
            this.timeout = Common.seconds();
        }
    }
}
