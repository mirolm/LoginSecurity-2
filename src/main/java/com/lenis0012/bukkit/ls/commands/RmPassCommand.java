package com.lenis0012.bukkit.ls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.ls.Lang;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;

public class RmPassCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		LoginSecurity plugin = LoginSecurity.instance;
		if(!(sender instanceof Player)) {
			sender.sendMessage(Lang.MUST_BE_PLAYER.toString());
			return true;
		}

		Player player = (Player)sender;
		String uuid = player.getUniqueId().toString();

		if(!plugin.data.checkUser(uuid)) {
			player.sendMessage(ChatColor.RED + Lang.NOT_REG.toString());
			return true;
		}if(args.length < 1) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_ARGS.toString());
			player.sendMessage(ChatColor.RED + Lang.USAGE.toString() + cmd.getUsage());
			return true;
		} if(!PasswordManager.checkPass(uuid, args[0])) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_PSW.toString());
			return true;
		} if(plugin.required) {
			player.sendMessage(ChatColor.RED + Lang.REQUIRED_PSW.toString());
			return true;
		}

		plugin.data.removeUser(uuid);
		player.sendMessage(ChatColor.GREEN + Lang.REMOVED_PSW.toString());
		return true;
	}
}
