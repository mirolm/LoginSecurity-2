package com.lenis0012.bukkit.ls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.ls.Lang;
import com.lenis0012.bukkit.ls.LoginSecurity;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;
import com.lenis0012.bukkit.ls.data.LoginData;
import java.util.logging.Level;

public class ChangePassCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		LoginSecurity plugin = LoginSecurity.instance;
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.MUST_BE_PLAYER.toString());
			return true;
		}

		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();

		if (!plugin.data.isRegistered(uuid)) {
			player.sendMessage(ChatColor.RED + Lang.NOT_REG.toString());
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_ARGS.toString());
			player.sendMessage(ChatColor.RED + Lang.USAGE.toString() + cmd.getUsage());
			return true;
		}
		if (!PasswordManager.checkPass(uuid, args[0])) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_PSW.toString());
			LoginSecurity.log.log(Level.WARNING, "[LoginSecurity] {0} failed to change password", player.getName());
			return true;
		}

		LoginData login = new LoginData(uuid, plugin.hasher.hash(args[1]), plugin.hasher.getTypeId(), null);
		plugin.data.updatePassword(login);

		player.sendMessage(ChatColor.GREEN + Lang.PSW_CHANGED.toString());
		LoginSecurity.log.log(Level.INFO, "[LoginSecurity] {0} sucessfully changed password", player.getName());

		return true;
	}
}
