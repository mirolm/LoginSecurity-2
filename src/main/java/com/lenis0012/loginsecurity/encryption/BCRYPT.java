package com.lenis0012.loginsecurity.encryption;

import org.mindrot.jbcrypt.BCrypt;

public class BCRYPT implements CryptManager {
    @Override
    public String hash(String passwd) {
        return BCrypt.hashpw(passwd, BCrypt.gensalt());
    }

    @Override
    public boolean check(String passwd, String hashed) {
        return BCrypt.checkpw(passwd, hashed);
    }
}