package com.lenis0012.bukkit.ls.encryption;

public enum EncryptionType {
    MD5(1, new MD5()),
    BCRYPT(7, new BCrypt());

    private final Encryptor cryp;
    private final int type;

    EncryptionType(int type, Encryptor cryp) {
        this.cryp = cryp;
        this.type = type;
    }

    public boolean checkPass(String check, String real) {
        return cryp.check(check, real);
    }

    public String hash(String value) {
        return cryp.hash(value);
    }

    public int getTypeId() {
        return type;
    }

    public static EncryptionType fromInt(int from) {
        for (EncryptionType type : values()) {
            if (type.type == from)
                return type;
        }
        return null;
    }

    public static EncryptionType fromString(String from) {
        if (from.equalsIgnoreCase("md5"))
            return MD5;
        if (from.equalsIgnoreCase("bcrypt")) {
            return BCRYPT;
        } else
            return BCRYPT;
    }
}
