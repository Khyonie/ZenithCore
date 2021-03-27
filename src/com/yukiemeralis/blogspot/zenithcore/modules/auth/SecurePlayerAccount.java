package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.annotations.Expose;
import com.yukiemeralis.blogspot.zenithcore.ZenithCore;

public class SecurePlayerAccount 
{
    @Expose(serialize = true, deserialize = true)
    String username;
    @Expose(serialize = true, deserialize = true)
    String password;
    @Expose(serialize = true, deserialize = true)
    AccountType type;

    public static enum AccountType {
        USER,
        ADMIN,
        SUPERADMIN
        ;
    }

    public SecurePlayerAccount(String user, String password, AccountType type)
    {
        this.username = user;
        this.password = genHash(password);
        this.type = type;
    }

    SecurePlayerAccount()
    {
        this.username = "console";
        this.password = genHash(ZenithCore.getSettings().getConsolePassword());
        this.type = AccountType.SUPERADMIN;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public AccountType getType()
    {
        return type;
    }

    public static boolean comparePassword(SecurePlayerAccount account, String input)
    {
        if (genHash(input).equals(account.getPassword()))
            return true;
        return false;
    }

    public static String genHash(String password)
    {
        try {   
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(password.getBytes());

            return new String(msgDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
