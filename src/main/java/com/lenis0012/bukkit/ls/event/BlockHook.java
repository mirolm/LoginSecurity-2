package com.lenis0012.bukkit.ls.event;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockHook implements Listener {
    private final LoginSecurity plugin;

    public BlockHook(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    private boolean authEntity(org.bukkit.entity.Entity entity) {
        if (entity instanceof org.bukkit.entity.Player) {
            Player player = (Player) entity;
            if (player.isOnline() && !player.hasMetadata("NPC")) {
                String uuid = player.getUniqueId().toString();

                return plugin.timeout.check(uuid);
            }
        }

        return false;
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
}
