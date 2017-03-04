package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.util.Common;

import java.util.Iterator;
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
        long cycle = Common.seconds();

        while (it.hasNext()) {
            String puuid = it.next();

            LockoutData current = faillist.get(puuid);
            if ((cycle - current.timeout) / 60 >= plugin.conf.minFail) {
                it.remove();
            }
        }
    }

    public boolean failed(String uuid) {
        if (faillist.containsKey(uuid)) {
            LockoutData current = faillist.get(uuid);

            current.failed += 1;
            current.timeout = Common.seconds();

            return faillist.replace(uuid, current).failed >= plugin.conf.countFail;
        } else {
            faillist.putIfAbsent(uuid, new LockoutData());

            return false;
        }
    }

    public boolean check(String uuid) {
        return faillist.containsKey(uuid) && (faillist.get(uuid).failed >= plugin.conf.countFail);
    }

    public void remove(String uuid) {
        faillist.remove(uuid);
    }

    class LockoutData {
        int failed;
        long timeout;

        LockoutData() {
            this.failed = 1;
            this.timeout = Common.seconds();
        }
    }
}
