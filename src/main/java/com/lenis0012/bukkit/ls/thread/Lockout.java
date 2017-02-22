package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Lockout implements Runnable {
    private final ConcurrentMap<String, LockoutData> faillist = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Lockout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = faillist.keySet().iterator();
        long cycle = seconds();

        while (it.hasNext()) {
            String puuid = it.next();

            LockoutData current = faillist.get(puuid);
            if ((cycle - current.timeout) / 60 >= plugin.conf.minFail) {
                it.remove();
            }
        }
    }

    public boolean failed(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        if (faillist.containsKey(fuuid)) {
            LockoutData current = faillist.get(fuuid);

            current.failed += 1;
            current.timeout = seconds();

            return faillist.replace(fuuid, current).failed >= plugin.conf.countFail;
        } else {
            faillist.putIfAbsent(fuuid, new LockoutData());

            return false;
        }
    }

    public boolean check(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        return faillist.containsKey(fuuid) && (faillist.get(fuuid).failed >= plugin.conf.countFail);
    }

    public void remove(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        faillist.remove(fuuid);
    }

    private String fulluuid(String uuid, String addr) {
        return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
    }

    private long seconds() {
        return System.currentTimeMillis() / 1000L;
    }

    class LockoutData {
        int failed;
        long timeout;

        LockoutData() {
            this.failed = 1;
            this.timeout = seconds();
        }
    }
}
