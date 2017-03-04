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
    private final Encryptor hasher;
    private final Logger logger;
    private final Executor executor;

    public Account(LoginSecurity plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.hasher = Encryptor.gethasher(plugin.conf.hasher);
        this.executor = new Executor(plugin);
    }

    public void disable() {
        executor.disable();
    }

    public boolean checkuser(String uuid) {
        return executor.check(uuid);
    }

    private boolean checkpass(String uuid, String password) {
        LoginData login = executor.get(uuid);

        return (hasher.gettype() == login.encryption) && hasher.check(password, login.password);
    }

    public boolean badname(String name) {
        // 3-16 chars long, letters and numbers
        return !name.matches("^\\w{3,16}$");
    }

    private boolean weakpass(String password) {
        // 6+ chars long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%\\^&*])).{6,}+$");
    }

    public void register(Player player, String pass) {
        if (Common.checkplayer(player)) {
            String uuid = Common.getuuid(player);

            if (checkuser(uuid)) {
                player.sendMessage(plugin.lang.get("already_reg"));

                return;
            }

            if (weakpass(pass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to register", player.getName());

                return;
            }

            executor.register(new LoginData(uuid, hasher.hash(pass), hasher.gettype()));

            plugin.timeout.remove(uuid);

            player.sendMessage(plugin.lang.get("registered"));
            logger.log(Level.INFO, "{0} registered successfully", player.getName());
        }
    }

    public void changepass(Player player, String oldpass, String newpass) {
        if (Common.checkplayer(player)) {
            String uuid = Common.getuuid(player);

            if (!checkpass(uuid, oldpass)) {
                player.sendMessage(plugin.lang.get("invalid_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            if (weakpass(newpass)) {
                player.sendMessage(plugin.lang.get("verify_psw"));
                logger.log(Level.WARNING, "{0} failed to change password", player.getName());

                return;
            }

            executor.update(new LoginData(uuid, hasher.hash(newpass), hasher.gettype()));

            player.sendMessage(plugin.lang.get("psw_changed"));
            logger.log(Level.INFO, "{0} successfully changed password", player.getName());
        }
    }

    public void login(Player player, String pass) {
        if (Common.checkplayer(player)) {
            String uuid = Common.getuuid(player);

            if (!plugin.timeout.check(uuid)) {
                player.sendMessage(plugin.lang.get("already_login"));

                return;
            }

            if (!checkuser(uuid)) {
                player.sendMessage(plugin.lang.get("no_psw_set"));

                return;
            }

            String fuuid = Common.fulluuid(player);

            if (checkpass(uuid, pass)) {
                plugin.timeout.remove(uuid);
                plugin.lockout.remove(fuuid);

                player.sendMessage(plugin.lang.get("login"));

                if (weakpass(pass)) {
                    player.sendMessage(plugin.lang.get("weak_psw"));
                    logger.log(Level.INFO, "{0} uses weak password", player.getName());
                }

                logger.log(Level.INFO, "{0} authenticated", player.getName());
            } else {
                if (plugin.lockout.failed(fuuid)) {
                    player.kickPlayer(plugin.lang.get("fail_count"));
                } else {
                    player.sendMessage(plugin.lang.get("invalid_psw"));
                    logger.log(Level.WARNING, "{0} entered an incorrect password", player.getName());
                }
            }
        }
    }
}
