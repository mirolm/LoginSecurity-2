package com.lenis0012.loginsecurity.command;

import com.lenis0012.loginsecurity.LoginSecurity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Register implements CommandExecutor {
    private final LoginSecurity plugin;

    public Register(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (args.length == 1)) {
            Player player = (Player) sender;
            plugin.account.registerPlayer(player, args[0]);
        }

        return true;
    }
}
