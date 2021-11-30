package com.lenis0012.loginsecurity.command;

import com.lenis0012.loginsecurity.LoginSecurity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {
    private final LoginSecurity plugin;

    public LoginCommand(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                plugin.account.loginPlayer(player, args[0]);
            } else {
                player.sendMessage(plugin.lang.get("cmd_usage").replace("<command>", cmd.getUsage()));
            }
        }

        return true;
    }
}
