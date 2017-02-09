package com.lenis0012.bukkit.ls.encryption;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.LoginData;

public class PasswordManager {
    public static boolean checkPass(String uuid, String password) {
        LoginSecurity plugin = LoginSecurity.instance;
        DataManager data = plugin.data;

        LoginData login = data.getUser(uuid);
        EncryptionType etype = EncryptionType.fromInt(login.encryption);

        if (etype != null) {
            return etype.checkPass(password, login.password);
        } else {
            return false;
        }
 	}
	
	public static boolean validPass(String password) {
		// 6+ chars long, letters and number or symbol
		return password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#\\$%\\^&\\*])).{6,}+$");	
	}
}
