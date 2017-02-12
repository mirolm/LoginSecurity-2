package com.lenis0012.bukkit.ls.commands;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginCommand implements CommandExecutor {
	private final LoginSecurity plugin;

	public LoginCommand(LoginSecurity plugin) {
		this.plugin = plugin;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Logger logger = plugin.getLogger();
		
		if(!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player)sender;
		String uuid = player.getUniqueId().toString();
		String addr = player.getAddress().getAddress().toString();
		String fuuid = plugin.getFullUUID(uuid, addr);

		if(!plugin.authList.containsKey(uuid)) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("already_login"));
			return true;
		}
		if(!plugin.data.checkUser(uuid)) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("no_psw_set"));
			return true;
		}
		if(args.length < 1) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("invalid_args"));
			player.sendMessage(ChatColor.RED + plugin.lang.get("usage") + cmd.getUsage());
			return true;
		}
		if(plugin.passmgr.checkPass(uuid, args[0])) {
			plugin.authList.remove(uuid);
			plugin.failList.remove(fuuid);
			plugin.thread.getTimeout().remove(uuid);
			plugin.rehabPlayer(player);

			player.sendMessage(ChatColor.GREEN + plugin.lang.get("login"));

			if(!plugin.passmgr.validPass(args[0])) {
				player.sendMessage(ChatColor.RED + plugin.lang.get("weak_psw"));
				logger.log(Level.INFO, "{0} uses weak password", player.getName());
			}

			logger.log(Level.INFO, "{0} authenticated", player.getName());
		} else {
			if (plugin.checkFailed(fuuid)) {
			        plugin.failList.remove(fuuid);
				plugin.thread.getLockout().put(fuuid, plugin.minFail);
				player.kickPlayer(plugin.lang.get("fail_count"));
			} else {
				player.sendMessage(ChatColor.RED + plugin.lang.get("invalid_psw"));
				logger.log(Level.WARNING, "{0} entered an incorrect password", player.getName());
			}
		}

		return true;
	}
}
