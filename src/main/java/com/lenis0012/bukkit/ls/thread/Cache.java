package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.Executor;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.util.Common;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class Cache implements Runnable {
    private final ConcurrentMap<String, CacheData> loginList = Maps.newConcurrentMap();
    private final Executor executor;

    public Cache(LoginSecurity plugin) {
        this.executor = new Executor(plugin);
    }

    @Override
    public void run() {
        Iterator<String> iterator = loginList.keySet().iterator();
        long cycle = Common.currentTime(false);

        while (iterator.hasNext()) {
            String uuid = iterator.next();

            CacheData current = loginList.get(uuid);

            if ((cycle - current.timeout) >= 5) {
                if (current.login != null) {
                    if ((cycle - current.timeout) <= 15) {
                        Player player = Common.getPlayer(uuid);
                        if (Common.checkPlayer(player)) {
                            refresh(uuid, null);
                        }
                    } else {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
    }

    private void refresh(String uuid, LoginData reload) {
        if (loginList.containsKey(uuid)) {
            CacheData current = loginList.get(uuid);

            if (reload != null) {
                current.login = reload;
            }

            current.timeout = Common.currentTime(false);

            loginList.replace(uuid, current);
        } else {
            LoginData login = executor.getLogin(uuid);

            loginList.putIfAbsent(uuid, new CacheData(login));
        }
    }

    public void disable() {
        executor.disable();
    }

    public boolean checkLogin(String uuid) {
        refresh(uuid, null);

        return loginList.get(uuid).login != null;
    }

    public LoginData getLogin(String uuid) {
        refresh(uuid, null);

        return loginList.get(uuid).login;
    }

    public void registerLogin(LoginData login) {
        executor.registerLogin(login);

        refresh(login.uuid, login);
    }

    public void updateLogin(LoginData login) {
        executor.updateLogin(login);

        refresh(login.uuid, login);
    }

    class CacheData {
        LoginData login;
        long timeout;

        CacheData(LoginData login) {
            this.login = login;
            this.timeout = Common.currentTime(false);
        }
    }
}
