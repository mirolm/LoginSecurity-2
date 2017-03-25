package com.lenis0012.bukkit.ls.util;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.Executor;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.encryption.Encryptor;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Account {
    private final LoginSecurity plugin;
    private final Encryptor crypt;
    private final Logger logger;
    private final Executor executor;

    public Account(LoginSecurity plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.crypt = Encryptor.getCrypt(plugin.config.encryption);
        this.executor = new Executor(plugin);
    }

    public void disable() {
        executor.disable();
    }

    public boolean checkLogin(String uuid) {
        return executor.checkLogin(uuid);
    }

    private boolean checkPassword(String uuid, String password) {
        LoginData login = executor.getLogin(uuid);

        return (crypt.getType() == login.encryption) && crypt.check(password, login.password);
    }

    public boolean invalidName(String name) {
        // 3-16 chars long, letters and numbers
        return !name.matches("^\\w{3,16}$");
    }

    private boolean weakPassword(String password) {
        // 6+ chars long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%&*])).{6,}+$");
    }

    public void registerPlayer(Player player, String pass) {
        if (Common.checkPlayer(player)) {
            String uuid = Common.getUuid(player);

            if (checkLogin(uuid)) {
                player.sendMessage(plugin.lang.get("already_reg"));

                return;
            }

            if (weakPassword(pass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to register", player.getName());

                return;
            }

            executor.registerLogin(new LoginData(uuid, crypt.hash(pass), crypt.getType()));

            plugin.timeout.remove(uuid);

            player.sendMessage(plugin.lang.get("registered"));
            logger.log(Level.INFO, "{0} registered successfully", player.getName());
        }
    }

    public void changePassword(Player player, String oldPass, String newPass) {
        if (Common.checkPlayer(player)) {
            String uuid = Common.getUuid(player);

            if (!checkPassword(uuid, oldPass)) {
                player.sendMessage(plugin.lang.get("invalid_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            if (weakPassword(newPass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            executor.updateLogin(new LoginData(uuid, crypt.hash(newPass), crypt.getType()));

            player.sendMessage(plugin.lang.get("psw_changed"));
            logger.log(Level.INFO, "{0} successfully changed password", player.getName());
        }
    }

    public void loginPlayer(Player player, String pass) {
        if (Common.checkPlayer(player)) {
            String uuid = Common.getUuid(player);

            if (!plugin.timeout.check(uuid)) {
                player.sendMessage(plugin.lang.get("already_login"));

                return;
            }

            if (!checkLogin(uuid)) {
                player.sendMessage(plugin.lang.get("no_psw_set"));

                return;
            }

            String fUuid = Common.fullUuid(player);

            if (checkPassword(uuid, pass)) {
                plugin.timeout.remove(uuid);
                plugin.lockout.remove(fUuid);

                player.sendMessage(plugin.lang.get("login"));

                if (weakPassword(pass)) {
                    player.sendMessage(plugin.lang.get("weak_psw"));
                    logger.log(Level.INFO, "{0} uses weak password", player.getName());
                }

                logger.log(Level.INFO, "{0} authenticated", player.getName());
            } else {
                if (plugin.lockout.failed(fUuid)) {
                    player.kickPlayer(plugin.lang.get("fail_count"));
                } else {
                    player.sendMessage(plugin.lang.get("invalid_psw"));
                    logger.log(Level.WARNING, "{0} entered an incorrect password", player.getName());
                }
            }
        }
    }
}
