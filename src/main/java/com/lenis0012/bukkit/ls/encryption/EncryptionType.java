package com.lenis0012.bukkit.ls.encryption;

public enum EncryptionType {
    MD5(1, new MD5()),
    BCRYPT(7, new BCrypt());

    private final Encryptor cryp;
    private final int type;

    EncryptionType(int type, Encryptor cryp) {
        this.type = type;
        this.cryp = cryp;
    }

    public static EncryptionType gethasher(String from) {
        if (from.equalsIgnoreCase("MD5"))
            return MD5;
        if (from.equalsIgnoreCase("BCRYPT")) {
            return BCRYPT;
        } else
            return BCRYPT;
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
