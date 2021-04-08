package com.yukiemeralis.blogspot.zenithcore.command;

import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;

public class CommandDescription 
{
    String description;
    AccountType access_type;

    public CommandDescription(String description)
    {
        this(description, AccountType.USER);
    }

    public CommandDescription(String description, AccountType access_type)
    {
        this.description = description;
        this.access_type = access_type;
    }

    public String getDesc()
    {
        return this.description;
    }

    public AccountType getAccess()
    {
        return this.access_type;
    }
}
