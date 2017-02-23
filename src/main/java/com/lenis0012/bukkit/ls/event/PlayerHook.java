package com.lenis0012.bukkit.ls.event;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Arrays;
import java.util.List;

public class PlayerHook implements Listener {
    private static final List<String> ALLOWED_COMMANDS = Arrays.asList("/login ", "/log ", "/l ", "/register ", "/reg ");
    private final LoginSecurity plugin;

    public PlayerHook(LoginSecurity plugin) {
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
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String pname = event.getName();
        //Check for valid user name
        if (!pname.matches("^\\w{3,16}$")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.lang.get("invalid_username"));

            return;
        }

        String uuid = event.getUniqueId().toString();
        String addr = event.getAddress().toString();
        //Check account locked due to failed logins
        if (plugin.lockout.check(uuid, addr)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.lang.get("account_locked"));

            return;
        }

        //Check if the player is already online
        for (org.bukkit.entity.Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.lang.get("already_online"));

                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        plugin.timeout.add(uuid, plugin.account.checkuser(uuid));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        plugin.timeout.remove(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent chat) {
        Player player = chat.getPlayer();
        if (authEntity(player)) {
            chat.setCancelled(true);
        }
    }

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
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
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (authEntity(player)) {
            String message = event.getMessage().toLowerCase();

            for (String cmd : ALLOWED_COMMANDS) {
                if (message.startsWith(cmd)) {
                    return;
                }
            }

            if (message.startsWith("/f")) {
                event.setMessage("/LOGIN_SECURITY_FACTION_REPLACEMENT_FIX");
            }

            event.setCancelled(true);
        }
    }
}
