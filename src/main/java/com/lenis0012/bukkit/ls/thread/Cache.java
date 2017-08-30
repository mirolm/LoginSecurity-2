package com.lenis0012.bukkit.ls.thread;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.Executor;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.encryption.Encryptor;
import com.lenis0012.bukkit.ls.util.Common;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class Cache implements Runnable {
    private final ConcurrentMap<String, CacheData> loginList = Maps.newConcurrentMap();
    private final Executor executor;
    private final LoginSecurity plugin;

    public Cache(LoginSecurity plugin) {
        this.executor = new Executor(plugin);
        this.plugin = plugin;
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
                    if ((cycle - current.timeout) <= 30) {
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

    private LoginData getLogin(String uuid) {
        refresh(uuid, null);

        return loginList.get(uuid).login;
    }

    public void modifyLogin(String uuid, String pass) {
        Encryptor crypt = Encryptor.getCrypt(plugin.config.encryption);
        LoginData login = new LoginData(uuid, crypt.hash(pass), crypt.getType());

        executor.modifyLogin(login);

        refresh(login.uuid, login);
    }

    public boolean checkLogin(String uuid) {
        return getLogin(uuid) != null;
    }

    public boolean checkPassword(String uuid, String pass) {
        LoginData login = getLogin(uuid);

        if (login != null) {
            Encryptor crypt = Encryptor.getCrypt(login.encryption);

            return crypt.check(pass, login.password);
        }

        return false;
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
