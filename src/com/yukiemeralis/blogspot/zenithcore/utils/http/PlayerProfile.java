package com.yukiemeralis.blogspot.zenithcore.utils.http;

import com.google.gson.annotations.Expose;

public class PlayerProfile 
{
    @Expose(serialize = true, deserialize = true)
    private String name, id;

    public PlayerProfile() {}
    
    public PlayerProfile(String name, String id)
    {
        this.name = name;
        this.id = id;
    }

    public String getUsername()
    {
        return name;
    }

    public String getUuid()
    {
        return id;
    }
}
