package com.lenis0012.loginsecurity.data;

import com.lenis0012.loginsecurity.LoginSecurity;

public class SQLExecutor {
    private final SQLManager data;

    public SQLExecutor(LoginSecurity plugin) {
        this.data = plugin.config.useMySQL ? new MySQLProvider(plugin) : new SQLiteProvider(plugin);

        if (plugin.config.convert) {
            SQLManager manager = plugin.config.useMySQL ? new SQLiteProvider(plugin) : new MySQLProvider(plugin);

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
        data.modifyLogin(login);
    }

    public void modifyDate(String uuid) {
        data.updateDate(uuid);
    }
}
