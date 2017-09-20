package com.lenis0012.loginsecurity.encryption;

public enum EncryptionProvider {
    BCRYPT(7, new BCryptProvider()),
    SCRYPT(21, new SCryptProvider());

    private final EncryptionManager crypt;
    private final int type;

    EncryptionProvider(int type, EncryptionManager crypt) {
        this.type = type;
        this.crypt = crypt;
    }

    public static EncryptionProvider getCrypt(String name) {
        for (EncryptionProvider encryptor : values()) {
            if (name.equalsIgnoreCase(encryptor.name())) {
                return encryptor;
            }
        }

        return BCRYPT;
    }

    public static EncryptionProvider getCrypt(int type) {
        for (EncryptionProvider encryptor : values()) {
            if (encryptor.type == type) {
                return encryptor;
            }
        }

        return BCRYPT;
    }

    public int getType() {
        return this.type;
    }

    public String hash(String passwd) {
        return crypt.hash(passwd);
    }

    public boolean check(String passwd, String hashed) {
        return crypt.check(passwd, hashed);
    }
}
