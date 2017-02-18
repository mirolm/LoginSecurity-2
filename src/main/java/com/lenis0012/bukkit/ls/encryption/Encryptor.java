package com.lenis0012.bukkit.ls.encryption;

public enum Encryptor {
    MD5(1, new MD5()),
    BCRYPT(7, new BCRYPT());

    private final CryptoManager cryp;
    private final int type;

    Encryptor(int type, CryptoManager cryp) {
        this.type = type;
        this.cryp = cryp;
    }

    public static Encryptor gethasher(String from) {
        if (from.equalsIgnoreCase("MD5")) {
            return MD5;
        } else if (from.equalsIgnoreCase("BCRYPT")) {
            return BCRYPT;
        } else {
            return BCRYPT;
        }
    }

    public int gettype() {
        return this.type;
    }

    public String hash(String value) {
        return cryp.hash(value);
    }

    public boolean check(String check, String real) {
        return cryp.check(check, real);
    }
}
