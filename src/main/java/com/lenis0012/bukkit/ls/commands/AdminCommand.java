package com.lenis0012.bukkit.ls.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.lenis0012.bukkit.ls.Lang;
import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		final LoginSecurity plugin = LoginSecurity.instance;
		if(!sender.hasPermission("ls.admin")) {
			sender.sendMessage(Lang.NO_PERM.toString());
			return true;
		}

		if(sender instanceof Player) {
			Player player = (Player) sender;
			String uuid = player.getUniqueId().toString();
			if(plugin.authList.containsKey(uuid)) {
				sender.sendMessage(Lang.MUST_LGN_FIRST.toString());
			}
		}

		try {
			if(args.length == 0) {
				sender.sendMessage("&7==========-{ &4&lL&aoginSecurity &4&lA&admin &4&lC&aommand &7}-==========".replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR)));
				sender.sendMessage(ChatColor.GREEN + "/lac rmpass <user>");
				sender.sendMessage(ChatColor.GREEN + "/lac reload");
			} else if(args.length >= 2 && args[0].equalsIgnoreCase("rmpass")) {
				String uuid = args[1];
				if(uuid != null && !uuid.isEmpty() && plugin.data.isRegistered(uuid)) {
					plugin.data.removeUser(uuid);
					sender.sendMessage(Lang.REM_USER_FROM_DB.toString());
				} else {
					sender.sendMessage(Lang.INVALID_USERNAME.toString());
				}
			} else if(args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
				plugin.reloadConfig();
				sender.sendMessage(Lang.RELOADED.toString());
			}

			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
