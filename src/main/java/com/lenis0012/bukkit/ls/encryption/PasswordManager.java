package com.lenis0012.bukkit.ls.encryption;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.LoginData;

public class PasswordManager {
    private final LoginSecurity plugin;

    public PasswordManager(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    public boolean checkPass(String uuid, String password) {
        DataManager data = plugin.data;

        LoginData login = data.getUser(uuid);
        EncryptionType etype = EncryptionType.fromInt(login.encryption);

        return (etype != null) && etype.checkPass(password, login.password);
    }

    public boolean weakPass(String password) {
        // 6+ chars long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%\\^&*])).{6,}+$");
    }
}
