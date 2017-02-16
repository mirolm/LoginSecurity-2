package com.lenis0012.bukkit.ls.commands;

import com.lenis0012.bukkit.ls.LoginSecurity;
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

		if(!plugin.timeout.check(uuid)) {
			player.sendMessage(plugin.lang.get("already_login"));
			return true;
		}
		if(!plugin.data.checkUser(uuid)) {
			player.sendMessage(plugin.lang.get("no_psw_set"));
			return true;
		}
		if(args.length < 1) {
			player.sendMessage(plugin.lang.get("invalid_args"));
			player.sendMessage(plugin.lang.get("usage") + cmd.getUsage());
			return true;
		}
		if(plugin.passmgr.checkPass(uuid, args[0])) {
			plugin.timeout.remove(uuid);
			plugin.lockout.remove(uuid, addr);

			player.sendMessage(plugin.lang.get("login"));

			if(!plugin.passmgr.validPass(args[0])) {
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

		return true;
	}
}
