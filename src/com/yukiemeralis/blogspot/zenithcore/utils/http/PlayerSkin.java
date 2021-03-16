package com.yukiemeralis.blogspot.zenithcore.utils.http;

import com.google.gson.annotations.Expose;

public class PlayerSkin 
{
    @Expose
    String value, signature;

    public PlayerSkin(String value, String signature)
    {
        this.value = value;
        this.signature = signature;
    }

    public String getValue()
    {
        return value;
    }

    public String getSignature()
    {
        return signature;
    }
}
