package com.yukiemeralis.blogspot.zenithcore.modules.core.listeners;

import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ZenithListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        PrintUtils.sendMessage(event.getPlayer(), "Welcome " + event.getPlayer().getDisplayName() + "! This server is running ZenithCore " + VersionCtrl.getVersion() + ".");
    }
}
