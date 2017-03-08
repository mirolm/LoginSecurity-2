package com.lenis0012.bukkit.ls.command;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Login implements CommandExecutor {
    private final LoginSecurity plugin;

    public Login(LoginSecurity plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            plugin.account.loginPlayer(player, args[0]);
        }

        return true;
    }
}
