package com.lenis0012.bukkit.ls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.ls.Lang;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;
import java.util.logging.Level;

public class LoginCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		LoginSecurity plugin = LoginSecurity.instance;
		if(!(sender instanceof Player)) {
			sender.sendMessage(Lang.MUST_BE_PLAYER.toString());
			return true;
		}

		Player player = (Player)sender;
		String uuid = player.getUniqueId().toString();

		if(!plugin.authList.containsKey(uuid)) {
			player.sendMessage(ChatColor.RED + Lang.ALREADY_LOGIN.toString());
			return true;
		}
		if(!plugin.data.isRegistered(uuid)) {
			player.sendMessage(ChatColor.RED + Lang.NO_PSW_SET.toString());
			return true;
		}
		if(args.length < 1) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_ARGS.toString());
			player.sendMessage(ChatColor.RED + Lang.USAGE + cmd.getUsage());
			return true;
		}
		if(PasswordManager.checkPass(uuid, args[0])) {
			plugin.authList.remove(uuid);
			plugin.thread.timeout.remove(uuid);
			plugin.rehabPlayer(player, uuid);
			player.sendMessage(ChatColor.GREEN + Lang.LOGIN.toString());
			LoginSecurity.log.log(Level.INFO, "[LoginSecurity] {0} authenticated", player.getName());
		} else {
			player.sendMessage(ChatColor.RED + Lang.INVALID_PSW.toString());
			LoginSecurity.log.log(Level.WARNING, "[LoginSecurity] {0} entered an incorrect password", player.getName());
		}

		return true;
	}
}
