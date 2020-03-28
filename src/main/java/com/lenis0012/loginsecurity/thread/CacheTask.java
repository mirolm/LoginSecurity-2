package com.lenis0012.loginsecurity.thread;

import com.google.common.collect.Maps;
import com.lenis0012.loginsecurity.LoginSecurity;
import com.lenis0012.loginsecurity.data.LoginData;
import com.lenis0012.loginsecurity.data.SQLExecutor;
import com.lenis0012.loginsecurity.encryption.EncryptionProvider;
import com.lenis0012.loginsecurity.util.CommonRoutines;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public class CacheTask implements Runnable {
    private final ConcurrentMap<String, CacheData> loginList = Maps.newConcurrentMap();
    private final SQLExecutor executor;
    private final LoginSecurity plugin;

    public CacheTask(LoginSecurity plugin) {
        this.executor = new SQLExecutor(plugin);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Iterator<String> iterator = loginList.keySet().iterator();
        long cycle = CommonRoutines.currentTime(false);

        while (iterator.hasNext()) {
            String uuid = iterator.next();

            CacheData current = loginList.get(uuid);

            if ((cycle - current.timeout) >= 5) {
                if (current.login != null) {
                    if ((cycle - current.timeout) <= 30) {
                        Player player = CommonRoutines.getPlayer(uuid);
                        if (CommonRoutines.checkPlayer(player)) {
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

            current.timeout = CommonRoutines.currentTime(false);

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
        EncryptionProvider crypt = EncryptionProvider.getCrypt(plugin.config.encryption);
        LoginData login = new LoginData(uuid, crypt.hash(pass), crypt.getType());

        executor.modifyLogin(login);

        refresh(login.uuid, login);
    }

    public void modifyDate(String uuid) {
        executor.modifyDate(uuid);
    }

    public boolean checkLogin(String uuid) {
        return getLogin(uuid) != null;
    }

    public boolean checkPassword(String uuid, String pass) {
        LoginData login = getLogin(uuid);

        if (login != null) {
            EncryptionProvider crypt = EncryptionProvider.getCrypt(login.encryption);

            return crypt.check(pass, login.password);
        }

        return false;
    }

    static class CacheData {
        LoginData login;
        long timeout;

        CacheData(LoginData login) {
            this.login = login;
            this.timeout = CommonRoutines.currentTime(false);
        }
    }
}
