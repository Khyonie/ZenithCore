package com.yukiemeralis.blogspot.zenithcore.modules.core.listeners;

import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount;
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

        ZenithCoreModule.getAccount(event.getPlayer());

        // Secure player account auto-login
        if (ZenithCoreModule.getAccount(event.getPlayer()).getAutoLogin())
        {
            SecurePlayerAccount sec_account = SecurityModule.getAccount(ZenithCoreModule.getAccount(event.getPlayer()).getAutoLoginUsername());

            // In case the secure player account list gets corrupt or something to that effect
            if (sec_account == null)
            {
                // Disable auto-login
                ZenithCoreModule.getAccount(event.getPlayer()).disableAutoLogin();
                return;
            }

            SecurityModule.loginAccount(event.getPlayer(), sec_account);
            PrintUtils.sendMessage(event.getPlayer(), "Auto-logged in as user \"" + SecurityModule.getLoggedInAccount(event.getPlayer()).getUsername() + "\".");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        if (SecurityModule.isLoggedIn(event.getPlayer()))
            SecurityModule.logoutAccount(event.getPlayer());

        JsonUtils.toJsonFile(JsonUtils.basepath + "accounts/" + event.getPlayer().getUniqueId().toString() + ".json", ZenithCoreModule.getAccount(event.getPlayer()));

        ZenithCoreModule.getAllAccounts().remove(event.getPlayer());
    }
}
