package com.yukiemeralis.blogspot.zenithcore.utils.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.net.ssl.HttpsURLConnection;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HttpManager 
{
    //
    // Timing bounds
    // Mojang's web API will block basic user profile requests if there have been more than 60 requests per minute.
    // It will also block full user profile requests if we try to request the same profile twice within a single minute.
    //
    private static int requests = 0; // Keep track of total requests
    static BukkitTask request_timer;
    static BukkitTask user_request_timer;

    private static ArrayList<String> requested_profiles = new ArrayList<>(); // Keep track of requested profiles

    static {
        request_timer = new BukkitRunnable()
        {
            @Override
            public void run() 
            {
                requests = 0;
            }
        }.runTaskTimer(ZenithCore.getInstance(), 0L, 600*20L);

        user_request_timer = new BukkitRunnable()
        {
            @Override
            public void run() 
            {
                // Since getSkinnedUserProfile() might be called at the same time as this thread runs, catch a possible exception
                try {
                    requested_profiles.clear();
                } catch (ConcurrentModificationException e) {}
            }
        }.runTaskTimer(ZenithCore.getInstance(), 60*20L, 60*20L);
    }

    public static PlayerProfile getUserProfile(String username)
    {
        PlayerProfile profile;

        if (requests >= 60)
        {
            // Attempt to load a stand-in profile if we don't want to try and reach the web API
            if (ZenithCore.getProfileManager().getAllProfiles().containsKey(username))
            {
                SkinnedPlayerProfile buffer = ZenithCore.getProfileManager().getProfile(username);
                profile = new PlayerProfile(buffer.getName(), buffer.getUUID());
                return profile;
            }

            // Otherwise return null;
            PrintUtils.sendMessage("Could not load profile of user " + username + ". (Too many requests. Try again later.)");
            return null;
        }

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.mojang.com/users/profiles/minecraft/" + username)).openConnection();

            requests++;

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) 
            {
                profile = JsonUtils.getUglyGson().fromJson(
                    new BufferedReader(new InputStreamReader(connection.getInputStream())), 
                    PlayerProfile.class
                );

                PrintUtils.sendMessage("Pulled profile of user: " + username + ". Profile name: " + profile.getUsername() + ", UUID: " + profile.getUuid());
            } else {
                PrintUtils.sendMessage("ERROR: Could not load profile from mojang web API!");
                profile = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (profile == null)
        {
            PrintUtils.sendMessage("ERROR: Could not load profile from mojang web API!");
        }
        return profile;
    }

    public static SkinnedPlayerProfile getSkinnedUserProfile(PlayerProfile profile)
    {
        SkinnedPlayerProfile buffer;

        // Attempt to pull cached profile
        if (ZenithCore.getProfileManager().containsProfile(profile.getUsername()))
        {
            return ZenithCore.getProfileManager().getProfile(profile.getUsername());
        }

        // Otherwise pull it from mojang's web API

        // Check to see if we haven't tried to pull this profile already
        // This will likely only fire if a user pulls a profile, deletes it immediately, and tries to pull it again.
        if (requested_profiles.contains(profile.getUsername()))
        {
            PrintUtils.sendMessage("WARN: Duplicate attempt to pull profile \"" + profile.getUsername() + "\".");
            return null;
        }

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/" + profile.getUuid() + "?unsigned=false")).openConnection();

            requested_profiles.add(profile.getUsername());

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) 
            {
                buffer = JsonUtils.getGson().fromJson(
                    new BufferedReader(new InputStreamReader(connection.getInputStream())), 
                    SkinnedPlayerProfile.class
                );

                // Verify the profile
                if (buffer.getName() == null)
                {
                    PrintUtils.sendMessage("ERROR: Requested profile " + profile.getUsername() + " does not exist.");
                    return null;
                }
            } else {
                PrintUtils.sendMessage("ERROR: Could not load skinned profile from mojang web API!");
                buffer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Cache profile
        ZenithCore.getProfileManager().cacheProfile(buffer);
        return buffer;
    }
}
