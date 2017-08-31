package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Executor {
    private final LoginSecurity plugin;
    private final SQLManager data;

    public Executor(LoginSecurity plugin) {
        this.plugin = plugin;

        this.data = plugin.config.useMySQL ? new MySQL(plugin) : new SQLite(plugin);

        convert();
    }

    public void disable() {
        data.close();
    }

    public LoginData getLogin(String uuid) {
        return data.getLogin(uuid);
    }

    public void modifyLogin(LoginData login) {
        if (data.checkLogin(login.uuid)) {
            data.updateLogin(login);
        } else {
            data.registerLogin(login);
        }
    }

    public void modifyDate(String uuid) {
        data.updateDate(uuid);
    }

    private void convert() {
        SQLManager manager;

        if (SQLite.exists(plugin) && plugin.config.useMySQL) {
            manager = new SQLite(plugin);

            try {
                data.convertAllLogin(manager);
            } finally {
                manager.close();
            }
        }
    }
}
