package com.lenis0012.bukkit.ls;

import java.util.List;
import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class LoginListener implements Listener {
	private LoginSecurity plugin;
	private final List<String> ALLOWED_COMMANDS = Lists.newArrayList("/login ", "/register ");

	public LoginListener(LoginSecurity i) {
		this.plugin = i;
	}

	private boolean authEntity(Entity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			String uuid = player.getUniqueId().toString();

			return plugin.authList.containsKey(uuid);
		}
		
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();

		if (plugin.sesUse && plugin.thread.getSession().containsKey(uuid) && plugin.checkLastIp(player)) {
			player.sendMessage(ChatColor.GREEN + Lang.SESS_EXTENDED.toString());
			return;
		} else if (plugin.data.checkUser(uuid)) {
			plugin.authList.put(uuid, false);
			player.sendMessage(ChatColor.RED + Lang.LOG_MSG.toString());
		} else if (plugin.required) {
			plugin.authList.put(uuid, true);
			player.sendMessage(ChatColor.RED + Lang.REG_MSG.toString());
		} else {
			return;
		}

		plugin.debilitatePlayer(player, uuid, false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		String pname = event.getName();
		//Check for valid user name
		if (!pname.equals(pname.replaceAll("[^a-zA-Z_0-9]", ""))) {
			event.disallow(Result.KICK_OTHER, Lang.INVALID_USERNAME_CHARS.toString());
			return;
 		}

		String uuid = event.getUniqueId().toString();
		//Check if the player is already online
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
				event.disallow(Result.KICK_OTHER, Lang.ALREADY_ONLINE.toString());
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();

		if (plugin.data.checkUser(uuid)) {
			plugin.updateLastIp(player);
			if (plugin.sesUse && !plugin.authList.containsKey(uuid)) {
				plugin.thread.getSession().put(uuid, plugin.sesDelay);
			}
		}

		plugin.authList.remove(uuid);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			Location from = event.getFrom();
			Location to = event.getTo();

			if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            			event.setTo(event.getFrom());
        		}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent chat) {
		Player player = chat.getPlayer();
		if (authEntity(player)) {
			chat.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Entity entity = event.getEntity();
		if (authEntity(entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerEditBook(PlayerEditBookEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Entity entity = event.getWhoClicked();
		if (authEntity(entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
 	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
 		Entity defender = event.getEntity();
 		Entity damager = event.getDamager();
 
 		if (authEntity(defender)) {
 			event.setCancelled(true);
 			return;
 		}
 
 		if (authEntity(damager)) {
 			event.setCancelled(true);
 		}
 	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {
		for (Entity entity : event.getAffectedEntities()) {
			if (authEntity(entity)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {
		Entity entity = event.getTarget();
		if (authEntity(entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityShootBow(EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		if (authEntity(entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (authEntity(player)) {
			String message = event.getMessage().toLowerCase();
        		for(String cmd : ALLOWED_COMMANDS) {
            			if(message.startsWith(cmd)) {
                			return;
            			}
        		}

			if(message.startsWith("/f")) {
            			event.setMessage("/LOGIN_SECURITY_FACTION_REPLACEMENT_FIX");
        		}

			event.setCancelled(true);
		}
	}
}
