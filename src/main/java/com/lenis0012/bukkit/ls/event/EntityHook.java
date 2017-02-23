package com.lenis0012.bukkit.ls.event;

import com.lenis0012.bukkit.ls.LoginSecurity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public class EntityHook implements Listener {
    private final LoginSecurity plugin;

    public EntityHook(LoginSecurity plugin) {
        this.plugin = plugin;
    }

    private boolean authEntity(org.bukkit.entity.Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.isOnline() && !player.hasMetadata("NPC")) {
                String uuid = player.getUniqueId().toString();

                return plugin.timeout.check(uuid);
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAirChange(EntityAirChangeEvent event) {
        Entity entity = event.getEntity();
        if (authEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity damager = event.getDamager();

        if (authEntity(defender) || authEntity(damager)) {
            event.setCancelled(true);
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
    public void onPotionSplash(PotionSplashEvent event) {
        for (Entity entity : event.getAffectedEntities()) {
            if (authEntity(entity)) {
                event.setCancelled(true);
            }
        }
    }
}
