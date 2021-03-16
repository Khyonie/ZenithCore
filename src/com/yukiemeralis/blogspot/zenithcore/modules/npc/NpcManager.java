package com.yukiemeralis.blogspot.zenithcore.modules.npc;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.yukiemeralis.blogspot.zenithcore.modules.npc.base.ZenithNPC;
import com.yukiemeralis.blogspot.zenithcore.modules.npc.base.ZenithNPCEntity;
import com.yukiemeralis.blogspot.zenithcore.utils.PacketUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.http.SkinnedPlayerProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class NpcManager 
{
    private static HashMap<String, ZenithNPC> npcs = new HashMap<>();
    
    public static HashMap<String, ZenithNPC> getNpcs()
    {
        return npcs;
    }

    public static ZenithNPC createNPC(String name, SkinnedPlayerProfile profile, Location location, boolean spawnOnServerStart)
    {
        EntityPlayer player = generatePlayerEntity(name, location);
        ZenithNPCEntity host = generateHostEntity(location);

        String alias = name;
        if (npcs.containsKey(name))
        {
            alias = name;
            name = name + "_";
        }

        player.getProfile().getProperties().put("textures", new Property("textures", profile.getSkin().getValue(), profile.getSkin().getSignature()));
        ZenithNPC npc = new ZenithNPC(alias, profile.getName(), player);

        if (alias != null)
            npc.setAlias(alias);

        npc.linkHost(host);

        npcs.put(name, npc);
        return npc;
    }

    public static void spawnNPC(ZenithNPC npc, List<Player> visibleTo)
    {
        npc.getEntityPlayer().setLocation(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), npc.getLocation().getYaw(), npc.getLocation().getPitch());
        
        ((CraftWorld) npc.getLocation().getWorld()).getHandle().addEntity(npc.getHost());

        visibleTo.forEach(player -> {
            PrintUtils.sendMessage("Showing NPC to " + player.getDisplayName());
            PacketUtils.showNPC(npc, player);
            PacketUtils.teleportEntity(npc.getEntityPlayer(), npc.getLocation(), player);
        });   
    }

    private static EntityPlayer generatePlayerEntity(String name, Location location)
    {
        MinecraftServer nms_server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nms_world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);

        return new EntityPlayer(nms_server, nms_world, profile, new PlayerInteractManager(nms_world));
    }

    private static ZenithNPCEntity generateHostEntity(Location location)
    {
        return new ZenithNPCEntity(EntityTypes.ZOMBIE, location);
    }
}
