package com.lenis0012.bukkit.ls.commands;

import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.LoginData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterCommand implements CommandExecutor {
	private final LoginSecurity plugin;

	public RegisterCommand(LoginSecurity plugin) {
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

		if (plugin.data.checkUser(uuid)) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("already_reg"));
			return true;
		}
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("invalid_args"));
			player.sendMessage(ChatColor.RED + plugin.lang.get("usage") + cmd.getUsage());
			return true;
		}
		if (!plugin.passmgr.validPass(args[0])) {
			player.sendMessage(ChatColor.RED + plugin.lang.get("verify_psw"));
			logger.log(Level.WARNING, "{0} failed to register", player.getName());
			return true;
		}

		LoginData login = new LoginData(uuid, plugin.hasher.hash(args[0]), plugin.hasher.getTypeId());
		plugin.data.regUser(login);

		plugin.authList.remove(uuid);
		plugin.thread.getTimeout().remove(uuid);
		plugin.rehabPlayer(player);

		player.sendMessage(ChatColor.GREEN + plugin.lang.get("registered"));
		logger.log(Level.INFO, "{0} registered sucessfully", player.getName());

		return true;
	}
}
