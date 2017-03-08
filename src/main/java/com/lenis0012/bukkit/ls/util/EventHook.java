package com.lenis0012.bukkit.ls.util;

import com.lenis0012.bukkit.ls.LoginSecurity;
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

public class EventHook implements Listener {
    private static final List<String> ALLOWED_COMMANDS = Arrays.asList("/login ", "/log ", "/l ", "/register ", "/reg ");
    private final LoginSecurity plugin;

    public EventHook(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    private boolean authEntity(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (Common.checkPlayer(player)) {
                String uuid = Common.getUuid(player);

                return plugin.timeout.check(uuid);
            }
        }

        return false;
    }

    //////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String pname = event.getName();
        //Check for valid user name
        if (plugin.account.badname(pname)) {
            event.disallow(Result.KICK_OTHER, plugin.lang.get("invalid_username"));

            return;
        }

        String fuuid = Common.fullUuid(event);
        //Check account locked due to failed logins
        if (plugin.lockout.check(fuuid)) {
            event.disallow(Result.KICK_OTHER, plugin.lang.get("account_locked"));

            return;
        }

        String uuid = Common.getUuid(event);
        //Check if the player is already online
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (uuid.equalsIgnoreCase(Common.getUuid(p))) {
                event.disallow(Result.KICK_OTHER, plugin.lang.get("already_online"));

                return;
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = Common.getUuid(player);

        plugin.timeout.add(uuid, plugin.account.checkuser(uuid));
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = Common.getUuid(player);

        plugin.timeout.remove(uuid);
    }

    //////////////////////////// BLOCK ///////////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    //////////////////////// INVENTORY ////////////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Entity entity = event.getWhoClicked();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    ///////////////////////////////// ENTITY //////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAirChange(EntityAirChangeEvent event) {
        Entity entity = event.getEntity();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity damager = event.getDamager();

        if (authEntity(defender) || authEntity(damager)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getTarget();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        for (Entity entity : event.getAffectedEntities()) {
            if (authEntity(entity)) {
                event.setCancelled(true);
            }
        }
    }

    //////////////////////////////// PLAYER ///////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent chat) {
        Player player = chat.getPlayer();
        if (authEntity(player)) {
            chat.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            String message = event.getMessage();
            if (Common.messageContains(message, ALLOWED_COMMANDS)) {
                return;
            }

            event.setCancelled(true);
        }
    }

    //////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }
}
