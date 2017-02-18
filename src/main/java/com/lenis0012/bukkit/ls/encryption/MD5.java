package com.lenis0012.bukkit.ls.encryption;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 implements CryptoManager {
    @Override
    public String hash(String pw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(pw.getBytes());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean check(String pw, String hashed) {
        return hash(pw).compareTo(hashed) == 0;
    }
}