package com.yukiemeralis.blogspot.zenithcore.modules.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurityModule;

import org.bukkit.entity.Player;

public class PlayerAccount 
{
    @Expose(serialize = true, deserialize = true)
    String uuid;
    @Expose(serialize = true, deserialize = true)
    List<String> sec_plr_acct_list = new ArrayList<>(); // Keep a list of secure player accounts tied to this user

    // Auto-login for secure player account
    @Expose(serialize = true, deserialize = true)
    String username;
    @Expose(serialize = true, deserialize = true)
    boolean autologin = false;

    /**
     * Blank constructor for GSON. Don't use it, please use PlayerAccount#<init>(Player player) instead.
     * @deprecated
     */
    public PlayerAccount() {}

    public PlayerAccount(Player player)
    {
        this.uuid = player.getUniqueId().toString();
    }

    public boolean setAutoLogin(String username)
    {
        if (SecurityModule.getAccount(username) == null)
            return false;

        this.username = username;
        this.autologin = true;

        return true;
    }

    public void disableAutoLogin()
    {
        autologin = false;
    }

    public String getUUID()
    {
        return this.uuid;
    }

    public List<String> getOwnedSecureAccounts()
    {
        return this.sec_plr_acct_list;
    }

    public String getAutoLoginUsername()
    {
        return this.username;
    }

    public boolean getAutoLogin()
    {
        return this.autologin;
    }
}
