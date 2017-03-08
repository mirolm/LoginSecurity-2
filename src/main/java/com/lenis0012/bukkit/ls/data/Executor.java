package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Executor {
    private final LoginSecurity plugin;
    private final SQLManager data;

    public Executor(LoginSecurity plugin) {
        this.plugin = plugin;

        this.data = plugin.config.useMysql ? new MySQL(plugin) : new SQLite(plugin);

        convert();
    }

    public void disable() {
        data.close();
    }

    public boolean checkLogin(String uuid) {
        return data.checkLogin(uuid);
    }

    public LoginData getLogin(String uuid) {
        return data.getLogin(uuid);
    }

    public void registerLogin(LoginData login) {
        if (!data.checkLogin(login.uuid)) {
            data.registerLogin(login);
        }
    }

    public void updateLogin(LoginData login) {
        if (data.checkLogin(login.uuid)) {
            data.updateLogin(login);
        }
    }

    private void convert() {
        SQLManager manager;

        if (SQLite.exists(plugin) && plugin.config.useMysql) {
            manager = new SQLite(plugin);

            try {
                data.convertAllLogin(manager);
            } finally {
                manager.close();
            }
        }
    }
}
