package com.lenis0012.loginsecurity.encryption;

import com.lambdaworks.crypto.SCryptUtil;

public class SCryptProvider implements EncryptionManager {
    @Override
    public String hash(String passwd) {
        return SCryptUtil.scrypt(passwd, 16384, 8, 1);
    }

    @Override
    public boolean check(String passwd, String hashed) {
        return SCryptUtil.check(passwd, hashed);
    }
}