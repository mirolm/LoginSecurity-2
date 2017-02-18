package com.lenis0012.bukkit.ls.command;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePass implements CommandExecutor {
    private final LoginSecurity plugin;

    public ChangePass(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        plugin.account.changepass(player, args[0], args[1]);

        return true;
    }
}
