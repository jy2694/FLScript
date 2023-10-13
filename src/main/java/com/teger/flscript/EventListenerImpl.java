package com.teger.flscript;

import com.teger.flscript.exception.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class EventListenerImpl implements Listener {

    private final FLScript plugin;

    public EventListenerImpl(FLScript instance){
        plugin = instance;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(e.getFrom().getBlockX() == e.getTo().getBlockX()
            && e.getFrom().getBlockY() == e.getTo().getBlockY()
            && e.getFrom().getBlockZ() == e.getTo().getBlockZ())
            return;
        plugin.runLoadedScriptByType(FLEventType.PlayerMove, new HashMap<>());
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e){
        Player player = e.getPlayer();
        plugin.runLoadedScriptByType(FLEventType.PlayerBreakBlock, new HashMap<>());
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        plugin.runLoadedScriptByType(FLEventType.PlayerPlaceBlock, new HashMap<>());
    }

    @EventHandler
    public void onPlaceBlock(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        plugin.runLoadedScriptByType(FLEventType.PlayerInteractBlock, new HashMap<>());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        Entity damager = e.getDamager();
        if(!(damager instanceof Player)) return;
        plugin.runLoadedScriptByType(FLEventType.PlayerHitEntity, new HashMap<>());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        plugin.runLoadedScriptByType(FLEventType.PlayerDamage, new HashMap<>());
    }
}
