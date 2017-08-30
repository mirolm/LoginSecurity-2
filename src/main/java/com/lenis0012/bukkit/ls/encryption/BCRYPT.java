package com.lenis0012.bukkit.ls.encryption;

import org.mindrot.jbcrypt.BCrypt;

public class BCRYPT implements CryptManager {
    @Override
    public String hash(String pass) {
        return BCrypt.hashpw(pass, BCrypt.gensalt());
    }

    @Override
    public boolean check(String pass, String hash) {
        return BCrypt.checkpw(pass, hash);
    }
}