package com.lenis0012.bukkit.ls;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.Arrays;
import java.util.List;

public class LoginListener implements Listener {
	private final LoginSecurity plugin;
	private static final List<String> ALLOWED_COMMANDS = Arrays.asList("/login ", "/log ", "/l ", "/register ", "/reg ");

	public LoginListener(LoginSecurity plugin) {
		this.plugin = plugin;
	}

	private boolean authEntity(Entity entity) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.isOnline() && !player.hasMetadata("NPC")) {
				String uuid = player.getUniqueId().toString();

				return plugin.timeout.check(uuid);
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();

		if (plugin.data.checkUser(uuid)) {
			plugin.timeout.add(uuid,false);
		} else if (plugin.conf.required) {
            plugin.timeout.add(uuid,true);
		} else {
			return;
		}

		plugin.debilitatePlayer(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		String pname = event.getName();
		//Check for valid user name
		if (!pname.matches("^\\w{3,16}$")) {
			event.disallow(Result.KICK_OTHER, plugin.lang.get("invalid_username"));
			return;
 		}

		String uuid = event.getUniqueId().toString();
		String addr = event.getAddress().toString();
		String fuuid = plugin.getFullUUID(uuid, addr);
		//Check account locked due to failed logins
		if (plugin.lockout.check(fuuid)) {
			event.disallow(Result.KICK_OTHER, plugin.lang.get("account_locked"));
			return;
		}

		//Check if the player is already online
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
			    event.disallow(Result.KICK_OTHER, plugin.lang.get("already_online"));
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();

		plugin.timeout.remove(uuid);
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
