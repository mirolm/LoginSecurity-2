package com.lenis0012.bukkit.ls.commands;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.LoginData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterCommand implements CommandExecutor {
    private final LoginSecurity plugin;

    public RegisterCommand(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Logger logger = plugin.getLogger();

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        if (plugin.data.checkUser(uuid)) {
            player.sendMessage(plugin.lang.get("already_reg"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.lang.get("invalid_args"));
            player.sendMessage(plugin.lang.get("usage") + cmd.getUsage());
            return true;
        }

        if (plugin.passmgr.weak(args[0])) {
            player.sendMessage(plugin.lang.get("verify_psw"));
            logger.log(Level.WARNING, "{0} failed to register", player.getName());
            return true;
        }

        LoginData login = new LoginData(uuid, plugin.passmgr.hash(args[0]), plugin.passmgr.gettypeid());
        plugin.data.regUser(login);

        plugin.timeout.remove(uuid);

        player.sendMessage(plugin.lang.get("registered"));
        logger.log(Level.INFO, "{0} registered sucessfully", player.getName());

        return true;
    }
}
