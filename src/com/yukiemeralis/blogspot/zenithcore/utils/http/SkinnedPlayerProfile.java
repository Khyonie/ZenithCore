package com.yukiemeralis.blogspot.zenithcore.utils.http;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.Deserializable;

public class SkinnedPlayerProfile implements Deserializable
{
    @Expose(serialize = true, deserialize = true)
    private String id, name;
    @Expose(serialize = true, deserialize = true)
    private ArrayList<PlayerSkin> properties;
    private PlayerSkin skin = null;

    public String getUUID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public PlayerSkin getSkin()
    {
        if (skin == null)
            deserialize();
        return skin;
    }

    @Override
    public void deserialize() 
    {
        skin = new PlayerSkin(properties.get(0).getValue(), properties.get(0).getSignature());
    }
}
