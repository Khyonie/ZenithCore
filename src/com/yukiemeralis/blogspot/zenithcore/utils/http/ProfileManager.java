package com.yukiemeralis.blogspot.zenithcore.utils.http;

import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

public class ProfileManager 
{
    @Expose(serialize = true, deserialize = true)
    private HashMap<String, SkinnedPlayerProfile> saved_profiles = new HashMap<>();

    public void saveToFile()
    {
        JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", this);
    }

    public HashMap<String, SkinnedPlayerProfile> getAllProfiles()
    {
        return saved_profiles;
    }

    public SkinnedPlayerProfile getProfile(String name)
    {
        return saved_profiles.get(name);
    }

    public boolean containsProfile(String name)
    {
        return saved_profiles.containsKey(name);
    }

    public void removeProfile(String name)
    {
        saved_profiles.remove(name);
    }

    public void cacheProfile(SkinnedPlayerProfile profile)
    {
        saved_profiles.put(profile.getName(), profile);
    } 
}
