package com.lenis0012.loginsecurity.thread;

import com.google.common.collect.Maps;
import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.Common;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class Lockout implements Runnable {
    private final ConcurrentMap<String, LockoutData> failList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public Lockout(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> iterator = failList.keySet().iterator();
        long cycle = Common.currentTime(false);

        while (iterator.hasNext()) {
            String uuid = iterator.next();

            LockoutData current = failList.get(uuid);
            if ((cycle - current.timeout) >= plugin.config.failedMinutes) {
                iterator.remove();
            }
        }
    }

    public boolean failed(String uuid) {
        if (failList.containsKey(uuid)) {
            LockoutData current = failList.get(uuid);

            current.failed += 1;
            current.timeout = Common.currentTime(false);

            return failList.replace(uuid, current).failed >= plugin.config.failedCount;
        } else {
            failList.putIfAbsent(uuid, new LockoutData());

            return false;
        }
    }

    public boolean check(String uuid) {
        return failList.containsKey(uuid) && (failList.get(uuid).failed >= plugin.config.failedCount);
    }

    public void remove(String uuid) {
        failList.remove(uuid);
    }

    class LockoutData {
        int failed;
        long timeout;

        LockoutData() {
            this.failed = 1;
            this.timeout = Common.currentTime(false);
        }
    }
}
