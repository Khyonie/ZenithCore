package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class AccountManager 
{
    @Expose(serialize = true, deserialize = true)
    private HashMap<String, SecurePlayerAccount> account_list = new HashMap<>();

    public HashMap<String, SecurePlayerAccount> getAccounts()
    {
        return account_list;
    }
}
