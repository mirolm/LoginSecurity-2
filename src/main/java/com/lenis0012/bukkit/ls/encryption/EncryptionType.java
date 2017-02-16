package com.lenis0012.bukkit.ls.encryption;

public enum EncryptionType {
    MD5(1, new MD5()),
    PHPBB3(2, new PHPBB3()),
    SHA1(3, new SHA("SHA-1")),
    SHA(4, new SHA("SHA")),
    SHA256(5, new SHA("SHA-256")),
    SHA512(6, new SHA("SHA-512")),
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
        for(EncryptionType type : values()) {
            if(type.type == from)
                return type;
        }
        return null;
    }
	
    public static EncryptionType fromString(String from) {
        if(from.equalsIgnoreCase("md5"))
            return MD5;
        else if(from.equalsIgnoreCase("phpbb3"))
            return PHPBB3;
        else if(from.equalsIgnoreCase("sha")) {
            return SHA;
        } else if(from.equalsIgnoreCase("sha-1")) {
            return SHA1;
        } else if(from.equalsIgnoreCase("sha-256")) {
            return SHA256;
        } else if(from.equalsIgnoreCase("sha-512")) {
            return SHA512;
        } else if(from.equalsIgnoreCase("bcrypt")) {
            return BCRYPT;
        } else
            return BCRYPT;
    }
}
