package com.lenis0012.loginsecurity.thread;

import com.google.common.collect.Maps;
import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.util.CommonRoutines;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class LockoutTask implements Runnable {
    private final ConcurrentMap<String, LockoutData> failList = Maps.newConcurrentMap();
    private final LoginSecurity plugin;

    public LockoutTask(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> iterator = failList.keySet().iterator();
        long cycle = CommonRoutines.currentTime(false);

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
            current.timeout = CommonRoutines.currentTime(false);

            failList.replace(uuid, current);

            return current.failed > plugin.config.failedCount;
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
            this.timeout = CommonRoutines.currentTime(false);
        }
    }
}
