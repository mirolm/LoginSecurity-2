package com.lenis0012.bukkit.ls.commands;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.LoginData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangePassCommand implements CommandExecutor {
	private final LoginSecurity plugin;

	public ChangePassCommand(LoginSecurity plugin) {
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

		if (!plugin.data.checkUser(uuid)) {
			player.sendMessage(plugin.lang.get("not_reg"));
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(plugin.lang.get("invalid_args"));
			player.sendMessage(plugin.lang.get("usage") + cmd.getUsage());
			return true;
		}
		if (!plugin.passmgr.checkPass(uuid, args[0])) {
			player.sendMessage(plugin.lang.get("invalid_psw"));
			logger.log(Level.WARNING, "{0} failed to change password", player.getName());
			return true;
		}
		if (!plugin.passmgr.validPass(args[1])) {
			player.sendMessage(plugin.lang.get("verify_psw"));
			logger.log(Level.WARNING, "{0} failed to change password", player.getName());
			return true;
		}

		LoginData login = new LoginData(uuid, plugin.hasher.hash(args[1]), plugin.hasher.getTypeId());
		plugin.data.updateUser(login);

		player.sendMessage(plugin.lang.get("psw_changed"));
		logger.log(Level.INFO, "{0} sucessfully changed password", player.getName());

		return true;
	}
}
