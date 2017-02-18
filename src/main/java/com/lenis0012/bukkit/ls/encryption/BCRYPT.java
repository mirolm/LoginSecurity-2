package com.lenis0012.bukkit.ls.encryption;

import org.mindrot.jbcrypt.BCrypt;

public class BCRYPT implements Encryptor {
    @Override
    public String hash(String pw) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(pw, salt);
    }

    @Override
    public boolean check(String pw, String hashed) {
        return BCrypt.checkpw(pw, hashed);
    }
}