package com.lenis0012.bukkit.ls.event;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryHook implements Listener {
    private final LoginSecurity plugin;

    public InventoryHook(LoginSecurity plugin) {
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
    public void onInventoryClick(InventoryClickEvent event) {
        Entity entity = event.getWhoClicked();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }
}
