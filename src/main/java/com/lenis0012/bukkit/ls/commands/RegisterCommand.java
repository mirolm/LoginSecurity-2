package com.lenis0012.bukkit.ls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.ls.Lang;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.data.LoginData;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RegisterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		LoginSecurity plugin = LoginSecurity.instance;
		Logger logger = plugin.getLogger();
		
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		String addr = player.getAddress().getAddress().toString();

		if (plugin.data.checkUser(uuid)) {
			player.sendMessage(ChatColor.RED + Lang.ALREADY_REG.toString());
			return true;
		}
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_ARGS.toString());
			player.sendMessage(ChatColor.RED + Lang.USAGE.toString() + cmd.getUsage());
			return true;
		}
		if (!PasswordManager.validPass(args[0])) {
			player.sendMessage(ChatColor.RED + Lang.VERIFY_PSW.toString());
			logger.log(Level.WARNING, "{0} failed to register", player.getName());
			return true;
		}

		LoginData login = new LoginData(uuid, plugin.hasher.hash(args[0]), plugin.hasher.getTypeId(), addr);
		plugin.data.regUser(login);

		plugin.authList.remove(uuid);
		plugin.thread.getTimeout().remove(uuid);
		plugin.rehabPlayer(player, uuid);

		player.sendMessage(ChatColor.GREEN + Lang.REGISTERED.toString());
		logger.log(Level.INFO, "{0} registered sucessfully", player.getName());

		return true;
	}
}
