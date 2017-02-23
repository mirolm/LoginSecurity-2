package com.lenis0012.bukkit.ls.encryption;

import org.mindrot.jbcrypt.BCrypt;

public class BCRYPT implements CryptoManager {
    @Override
    public String hash(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    @Override
    public boolean check(String pw, String hashed) {
        return BCrypt.checkpw(pw, hashed);
    }
}