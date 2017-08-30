package com.lenis0012.bukkit.ls.encryption;

interface CryptManager {
    /**
     * Check if passwords mach
     *
     * @param pass password to check
     * @param hash hashed password from database
     * @return password match
     */
    boolean check(String pass, String hash);

    /**
     * Hash a password
     *
     * @param pass password to hash
     * @return hashed value
     */
    String hash(String pass);
}
