package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Lockout implements Runnable {
    private class LockoutData {
        public int failed;
        public long timeout;

        public LockoutData() {
            this.failed = 1;
            this.timeout = System.currentTimeMillis() / 1000L;
        }
    }

    private final ConcurrentMap<String, LockoutData> failList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Lockout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = failList.keySet().iterator();
        long cycle = System.currentTimeMillis() / 1000L;

        while (it.hasNext()) {
            String puuid = it.next();
            if (check(puuid)) {
                LockoutData current = failList.get(puuid);
                if ((cycle - current.timeout) / 60 >= plugin.conf.minFail) {
                    it.remove();
                }
            }
        }
    }

    public boolean failed(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        LockoutData current = failList.putIfAbsent(fuuid, new LockoutData());

        current.failed += 1;
        current.timeout = System.currentTimeMillis() / 1000L;

        return failList.replace(fuuid, current).failed >= plugin.conf.countFail;
    }

    private boolean check(String uuid) {
        return failList.containsKey(uuid) && (failList.get(uuid).failed >= plugin.conf.countFail);
    }

    public boolean check(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        return check(fuuid);
    }

    public void remove(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        failList.remove(fuuid);
    }

    private String fulluuid(String uuid, String addr) {
        return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
    }
}
