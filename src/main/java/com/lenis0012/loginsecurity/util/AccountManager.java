package com.lenis0012.loginsecurity.util;

import com.lenis0012.loginsecurity.LoginSecurity;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountManager {
    private final LoginSecurity plugin;
    private final Logger logger;

    public AccountManager(LoginSecurity plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void registerPlayer(Player player, String pass) {
        if (CommonRoutines.checkPlayer(player)) {
            String uuid = CommonRoutines.getUuid(player);

            if (plugin.cache.checkLogin(uuid)) {
                player.sendMessage(plugin.lang.get("already_reg"));

                return;
            }

            if (CommonRoutines.invalidPassword(pass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to register", player.getName());

                return;
            }

            plugin.cache.modifyLogin(uuid, pass);

            plugin.timeout.remove(uuid);

            player.sendMessage(plugin.lang.get("registered"));
            logger.log(Level.INFO, "{0} registered successfully", player.getName());
        }
    }

    public void changePassword(Player player, String oldPass, String newPass) {
        if (CommonRoutines.checkPlayer(player)) {
            String uuid = CommonRoutines.getUuid(player);

            if (!plugin.cache.checkPassword(uuid, oldPass)) {
                player.sendMessage(plugin.lang.get("invalid_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            if (CommonRoutines.invalidPassword(newPass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            plugin.cache.modifyLogin(uuid, newPass);

            player.sendMessage(plugin.lang.get("psw_changed"));
            logger.log(Level.INFO, "{0} successfully changed password", player.getName());
        }
    }

    public void loginPlayer(Player player, String pass) {
        if (CommonRoutines.checkPlayer(player)) {
            String uuid = CommonRoutines.getUuid(player);

            if (!plugin.timeout.check(uuid)) {
                player.sendMessage(plugin.lang.get("already_login"));

                return;
            }

            if (!plugin.cache.checkLogin(uuid)) {
                player.sendMessage(plugin.lang.get("no_psw_set"));

                return;
            }

            String fUuid = CommonRoutines.fullUuid(player);

            if (plugin.cache.checkPassword(uuid, pass)) {
                plugin.timeout.remove(uuid);
                plugin.lockout.remove(fUuid);

                plugin.cache.modifyDate(uuid);

                player.sendMessage(plugin.lang.get("login"));

                if (CommonRoutines.invalidPassword(pass)) {
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
