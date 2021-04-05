package com.yukiemeralis.blogspot.zenithcore.modules.core.listeners;

import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurityModule;
import com.yukiemeralis.blogspot.zenithcore.modules.core.ZenithCoreModule;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ZenithListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        PrintUtils.sendMessage(event.getPlayer(), "Welcome " + event.getPlayer().getDisplayName() + "! This server is running ZenithCore " + VersionCtrl.getVersion() + ".");

        // Check accounts
        if (ZenithCoreModule.getAccount(event.getPlayer()) == null)
        {
            ZenithCoreModule.createAccount(event.getPlayer());
        }

        // Secure player account auto-login
        if (ZenithCoreModule.getAccount(event.getPlayer()).getAutoLogin())
        {
            SecurityModule.loginAccount(
                event.getPlayer(), SecurityModule.getAccount(
                    ZenithCoreModule.getAccount(event.getPlayer()).getAutoLoginUsername()
                )
            );

            PrintUtils.sendMessage(event.getPlayer(), "Auto-logged in as user \"" + SecurityModule.getLoggedInAccount(event.getPlayer()).getUsername() + "\".");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        if (SecurityModule.isLoggedIn(event.getPlayer()))
            SecurityModule.logoutAccount(event.getPlayer());

        JsonUtils.toJsonFile(JsonUtils.basepath + "RegularUserAccounts.json", ZenithCoreModule.getAllAccounts());
    }
}
