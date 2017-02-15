package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class LockoutThread extends BukkitRunnable {
    private class LockoutData {
        int failed;
        int timeout;

        public LockoutData(int failed, int timeout) {
            this.failed = failed;
            this.timeout = timeout;
        }
    }

    private final ConcurrentMap<String, LockoutData> failList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public LockoutThread(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> it = failList.keySet().iterator();
        while (it.hasNext()) {
            String puuid = it.next();
            if (check(puuid)) {
                LockoutData current = failList.get(puuid);
                if (current.timeout >= 1) {
                    current.timeout -= 1;
                    failList.put(puuid, current);
                } else {
                    it.remove();
                }
            }
        }
    }

    public boolean failed(String uuid) {
        if (failList.containsKey(uuid)) {
            LockoutData current = failList.get(uuid);
            current.failed += 1;

            return failList.put(uuid, current).failed  >= plugin.conf.countFail;
        } else {
            failList.put(uuid, new LockoutData(1, plugin.conf.minFail));
        }

        return false;
    }

    public boolean check(String uuid) {
        if (failList.containsKey(uuid)) {
            return failList.get(uuid).failed >= plugin.conf.countFail;
        } else {
            return false;
        }
    }

    public void remove(String uuid) {
        if (failList.containsKey(uuid)) {
            failList.remove(uuid);
        }
    }
}
