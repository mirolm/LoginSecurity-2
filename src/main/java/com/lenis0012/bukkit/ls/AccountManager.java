package com.lenis0012.bukkit.ls;

import com.lenis0012.bukkit.ls.data.Executor;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.encryption.EncryptionType;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountManager {
    private final LoginSecurity plugin;
    private final EncryptionType hasher;
    private final Logger logger;
    private final Executor executor;

    public AccountManager(LoginSecurity plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.hasher = EncryptionType.gethasher(plugin.conf.hasher);
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

    private boolean weakpass(String password) {
        // 6+ chars long, letters and number or symbol
        return !password.matches("^(?=.*[a-zA-Z])(?=.*([0-9]|[!@#$%\\^&*])).{6,}+$");
    }

    public void register(Player player, String pass) {
        if (!(player != null && player.isOnline())) {
            return;
        }

        String uuid = player.getUniqueId().toString();

        if (this.checkuser(uuid)) {
            player.sendMessage(plugin.lang.get("already_reg"));
            return;
        }

        if (this.weakpass(pass)) {
            player.sendMessage(plugin.lang.get("verify_psw"));
            logger.log(Level.WARNING, "{0} failed to register", player.getName());
            return;
        }

        executor.register(new LoginData(uuid, hasher.hash(pass), hasher.gettype()));

        plugin.timeout.remove(uuid);

        player.sendMessage(plugin.lang.get("registered"));
        logger.log(Level.INFO, "{0} registered sucessfully", player.getName());
    }

    public void changepass(Player player, String oldpass, String newpass) {
        if (!(player != null && player.isOnline())) {
            return;
        }

        String uuid = player.getUniqueId().toString();

        if (!this.checkpass(uuid, oldpass)) {
            player.sendMessage(plugin.lang.get("invalid_psw"));
            logger.log(Level.WARNING, "{0} failed to change password", player.getName());
            return;
        }

        if (this.weakpass(newpass)) {
            player.sendMessage(plugin.lang.get("verify_psw"));
            logger.log(Level.WARNING, "{0} failed to change password", player.getName());
            return;
        }

        executor.update(new LoginData(uuid, hasher.hash(newpass), hasher.gettype()));

        player.sendMessage(plugin.lang.get("psw_changed"));
        logger.log(Level.INFO, "{0} sucessfully changed password", player.getName());
    }

    public void login(Player player, String pass) {
        if (!(player != null && player.isOnline())) {
            return;
        }

        String uuid = player.getUniqueId().toString();
        String addr = player.getAddress().getAddress().toString();

        if (!plugin.timeout.check(uuid)) {
            player.sendMessage(plugin.lang.get("already_login"));
            return;
        }

        if (!this.checkuser(uuid)) {
            player.sendMessage(plugin.lang.get("no_psw_set"));
            return;
        }

        if (this.checkpass(uuid, pass)) {
            plugin.timeout.remove(uuid);
            plugin.lockout.remove(uuid, addr);

            player.sendMessage(plugin.lang.get("login"));

            if (this.weakpass(pass)) {
                player.sendMessage(plugin.lang.get("weak_psw"));
                logger.log(Level.INFO, "{0} uses weak password", player.getName());
            }

            logger.log(Level.INFO, "{0} authenticated", player.getName());
        } else {
            if (plugin.lockout.failed(uuid, addr)) {
                player.kickPlayer(plugin.lang.get("fail_count"));
            } else {
                player.sendMessage(plugin.lang.get("invalid_psw"));
                logger.log(Level.WARNING, "{0} entered an incorrect password", player.getName());
            }
        }
    }
}
