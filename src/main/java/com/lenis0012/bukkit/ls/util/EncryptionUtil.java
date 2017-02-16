package com.lenis0012.bukkit.ls.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class EncryptionUtil {
    public static String encrypt(String value, String algorithm, String encoding) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
            digest.update(value.getBytes(encoding));
            byte[] rawDigest = digest.digest();
            return Base64Coder.encodeLines(rawDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid encoding: " + encoding);
        }
    }
	
    public static String getMD5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes(), 0 ,value.length());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return value;
        }
    }
}
