package com.lenis0012.bukkit.ls.encryption;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.LoginData;

public class PasswordManager {
    private final LoginSecurity plugin;
    private final EncryptionType hasher;

    public PasswordManager(LoginSecurity plugin) {
        this.plugin = plugin;
        this.hasher = EncryptionType.gethasher(plugin.conf.hasher);
    }

    public String hash(String value) {
        return hasher.hash(value);
    }

    public int type() {
        return hasher.gettype();
    }

    public boolean check(String uuid, String password) {
        LoginData login = plugin.data.getUser(uuid);

        return (hasher.gettype() == login.encryption) && hasher.check(password, login.password);
    }

    public boolean weak(String password) {
        // 6+ chars long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%\\^&*])).{6,}+$");
    }
}
