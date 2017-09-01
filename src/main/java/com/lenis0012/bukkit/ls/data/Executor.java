package com.lenis0012.bukkit.ls.data;

import com.lenis0012.bukkit.ls.LoginSecurity;

public class Executor {
    private final SQLManager data;

    public Executor(LoginSecurity plugin) {
        this.data = plugin.config.useMySQL ? new MySQL(plugin) : new SQLite(plugin);

        if (plugin.config.convert) {
            SQLManager manager = plugin.config.useMySQL ? new SQLite(plugin) : new MySQL(plugin);

            try {
                data.convertAllLogin(manager);
            } finally {
                manager.close();
            }
        }
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
}
