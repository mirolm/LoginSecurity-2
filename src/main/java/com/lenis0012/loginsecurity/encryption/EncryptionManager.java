package com.lenis0012.loginsecurity.encryption;

interface EncryptionManager {
    /**
     * Check if passwords mach
     *
     * @param passwd password to check
     * @param hashed hashed password from database
     * @return password match
     */
    boolean check(String passwd, String hashed);

    /**
     * Hash a password
     *
     * @param passwd password to hash
     * @return hashed value
     */
    String hash(String passwd);
}
