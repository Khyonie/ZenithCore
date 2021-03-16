package com.yukiemeralis.blogspot.zenithcore.modules.npc.base;

import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.utils.PacketUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.http.PlayerSkin;
import com.yukiemeralis.blogspot.zenithcore.utils.http.SkinnedPlayerProfile;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.Deserializable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class ZenithNPC implements Deserializable
{
    @Expose
    private String name, alias, profilename;
    
    @Expose
    private boolean isAliased = false;

    @Expose
    private Location loc;

    @Expose
    private boolean spawnOnServerStart = false;

    private PlayerSkin skin;
    private ZenithNPCEntity host;
    private EntityPlayer player;

    private GameProfile profile;
    private DataWatcher watcher;

    /**
     * Default constructor for JSON. Not for developer use.
     */
    public ZenithNPC() {}

    public ZenithNPC(String name, String profilename, EntityPlayer player)
    {
        this.name = name;
        this.profilename = profilename;

        this.player = player;
        this.profile = player.getProfile();

        this.loc = player.getBukkitEntity().getLocation();

        skin = ZenithCore.getProfileManager().getProfile(profilename).getSkin();

        this.watcher = player.getDataWatcher();
        watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
    }

    public String getName()
    {
        return name;
    }

    public String getAlias()
    {
        if (alias == null)
            return null;
        return alias;
    }

    public boolean isAliased()
    {
        return isAliased;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
        this.isAliased = true;
    }

    public void deleteAlias()
    {
        this.alias = null;
        this.isAliased = false;
    }

    public SkinnedPlayerProfile getAttachedProfile()
    {
        return ZenithCore.getProfileManager().getProfile(profilename);
    }

    public PlayerSkin getSkin()
    {
        return skin;
    }

    public Location getLocation()
    {
        return loc;
    }

    public void update(Player target)
    {
        PacketUtils.teleportEntity(player, host.getBukkitEntity().getLocation(), target);
    }

    public ZenithNPCEntity getHost()
    {
        return host;
    }

    public EntityPlayer getEntityPlayer()
    {
        return player;
    }

    public GameProfile getProfile()
    {
        return profile;
    }

    public DataWatcher getWatcher()
    {
        return watcher;
    }

    // Setters
    public void linkHost(ZenithNPCEntity host)
    {
        this.host = host;
    }

    public void linkPlayerEntity(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void deserialize() 
    {
        skin = getAttachedProfile().getSkin();
    }
}
