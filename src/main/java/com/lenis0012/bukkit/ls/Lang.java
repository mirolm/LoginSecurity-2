package com.lenis0012.bukkit.ls;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
    INVALID_USERNAME("invalid_username", "Player uses invalid username!"),
    ALREADY_ONLINE("already_online", "A player with this name is already online!"),
    ACCOUNT_LOCKED("account_locked", "Account locked try again later"),
    TIMED_OUT("timed_out", "Login timed out"),
    FAIL_COUNT("fail_count", "Too many failed login attempts"),
    REG_MSG("reg_msg", "Please register using /register <password>"),
    LOG_MSG("log_msg", "Please login using /login <password>"),
    MUST_BE_PLAYER("must_be_player", "You must be a player"),
    NOT_REG("not_reg", "You are not registered on the server"),
    INVALID_ARGS("invalid_args", "Not enough arguments"),
    USAGE("usage", "Usage: "),
    INVALID_PSW("invalid_psw", "Password Incorrect"),
    VERIFY_PSW("verify_psw", "Password must be at least 6 characters long alphanumeric"),
    WEAK_PSW("weak_psw", "Password is weak please change it"),
    REQUIRED_PSW("required_psw", "Passwords are required on this server!"),
    ALREADY_REG("already_reg", "You are already registered"),
    REGISTERED("registered", "Registered successful"),
    MUST_LGN_FIRST("must_lgn_first", "You must login first"),
    LOGOUT("logout", "Succesfully logged out"),
    ALREADY_LOGIN("already_login", "You are already logged in"),
    NO_PSW_SET("no_psw_set", "You do not have a password set"),
    LOGIN("login", "Succesfully logged in"),
    PSW_CHANGED("psw_changed", "Sucessfully changed password"),
    SESS_EXTENDED("sess_extended", "Extended session from last login");

    private String path;
    private String def;
    private static YamlConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }
}
