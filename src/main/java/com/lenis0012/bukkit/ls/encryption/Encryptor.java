package com.lenis0012.bukkit.ls.encryption;

public enum Encryptor {
    MD5(1, new MD5()),
    BCRYPT(7, new BCRYPT()),
    SCRYPT(21, new SCRYPT());

    private final CryptManager crypt;
    private final int type;

    Encryptor(int type, CryptManager crypt) {
        this.type = type;
        this.crypt = crypt;
    }

    public static Encryptor getCrypt(String from) {
        if (from.equalsIgnoreCase("MD5")) {
            return MD5;
        } else if (from.equalsIgnoreCase("BCRYPT")) {
            return BCRYPT;
        } else if (from.equalsIgnoreCase("SCRYPT")) {
            return SCRYPT;
        } else {
            return BCRYPT;
        }
    }

    public static Encryptor getCrypt(int type) {
        for (Encryptor encryptor : values()) {
            if (encryptor.type == type) {
                return encryptor;
            }
        }

        return BCRYPT;
    }

    public int getType() {
        return this.type;
    }

    public String hash(String value) {
        return crypt.hash(value);
    }

    public boolean check(String check, String real) {
        return crypt.check(check, real);
    }
}
