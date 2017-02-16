package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class LockoutThread implements Runnable {
    private class LockoutData {
        public final String uuid;
        public int failed;
        public long timeout;

        public LockoutData(String uuid) {
            this.uuid = uuid;
            this.failed = 1;
            this.timeout = System.currentTimeMillis();
        }
    }

    private final ConcurrentMap<String, LockoutData> failList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;
    private long cycle;

    public LockoutThread(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = failList.keySet().iterator();
        cycle = System.currentTimeMillis() / 1000L;

        while (it.hasNext()) {
            String puuid = it.next();
            if (check(puuid)) {
                LockoutData current = failList.get(puuid);
                if (trigger(current)) {
                    it.remove();
                }
            }
        }
    }

    public boolean failed(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        if (failList.containsKey(fuuid)) {
            LockoutData current = failList.get(fuuid);

            current.failed += 1;
            current.timeout = System.currentTimeMillis() / 1000L;

            return failList.put(fuuid, current).failed  >= plugin.conf.countFail;
        } else {
            failList.put(fuuid, new LockoutData(fuuid));
        }

        return false;
    }

    private boolean check(String uuid) {
        if (failList.containsKey(uuid)) {
            return failList.get(uuid).failed >= plugin.conf.countFail;
        } else {
            return false;
        }
    }

    public boolean check(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        return check(fuuid);
    }

    public void remove(String uuid, String addr) {
        String fuuid = fulluuid(uuid, addr);

        if (failList.containsKey(fuuid)) {
            failList.remove(fuuid);
        }
    }

    private String fulluuid(String uuid, String addr) {
        return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
    }

    private boolean trigger(LockoutData current) {
        if (failList.containsKey(current.uuid)) {
            return (cycle - current.timeout) / 60 >= plugin.conf.minFail;
        } else {
            return false;
        }
    }
}
