package com.lenis0012.bukkit.ls.encryption;

import com.lambdaworks.crypto.SCryptUtil;

public class SCRYPT implements CryptManager {
    @Override
    public String hash(String pw) {
        return SCryptUtil.scrypt(pw, 16384, 8, 1);
    }

    @Override
    public boolean check(String pw, String hashed) {
        return SCryptUtil.check(pw, hashed);
    }
}