package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import org.bukkit.entity.Player;

public class AccountRequest 
{
    Player requester;
    SecurePlayerAccount account;
    String username;

    /**
     * Container for admin/superadmin account requests, for other admins/superadmins to approve.
     * @param requester
     * @param account
     */
    public AccountRequest(Player requester, SecurePlayerAccount account)
    {
        this.requester = requester;
        this.account = account;
        this.username = account.getUsername();
    }

    public Player getPlayer()
    {
        return requester;
    }

    public SecurePlayerAccount getAccount()
    {
        return account;
    }

    public String getUsername()
    {
        return account.getUsername();
    }
}
