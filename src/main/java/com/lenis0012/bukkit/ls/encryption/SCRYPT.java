package com.lenis0012.bukkit.ls.encryption;

import com.lambdaworks.crypto.SCryptUtil;

public class SCRYPT implements CryptManager {
    @Override
    public String hash(String pass) {
        return SCryptUtil.scrypt(pass, 16384, 8, 1);
    }

    @Override
    public boolean check(String pass, String hash) {
        return SCryptUtil.check(pass, hash);
    }
}