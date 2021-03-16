package com.yukiemeralis.blogspot.zenithcore.utils;

import com.yukiemeralis.blogspot.zenithcore.modules.npc.base.ZenithNPC;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class PacketUtils 
{
    public static void hideEntity(org.bukkit.entity.Entity entity, Player player)
    {
        hideEntity(((CraftEntity) entity).getHandle(), player);
    }

    public static void hideEntity(net.minecraft.server.v1_16_R3.Entity entity, Player player)
    {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));
    }

    public static void showNPC(ZenithNPC npc, Player player)
    {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        // Add the NPC to the server
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getEntityPlayer().getId(), npc.getWatcher(), true));

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc.getEntityPlayer()));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc.getEntityPlayer()));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc.getEntityPlayer(), (byte) (npc.getEntityPlayer().yaw * 256 / 360)));
    }

    public static void teleportEntity(net.minecraft.server.v1_16_R3.Entity entity, Location location, Player player)
    {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        connection.sendPacket(new PacketPlayOutEntityTeleport(entity));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte) (entity.yaw * 256 / 360)));
    }
}
