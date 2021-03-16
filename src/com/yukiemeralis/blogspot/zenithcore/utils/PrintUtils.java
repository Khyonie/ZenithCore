package com.yukiemeralis.blogspot.zenithcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class PrintUtils 
{
    public static void sendMessage(Entity target, String message)
    {
        target.sendMessage("§8[§dz§8] §7" + message);
    }

    public static void sendMessage(ConsoleCommandSender target, String message)
    {
        System.out.println("[z] " + message);
    }

    public static void sendMessage(CommandSender target, String message)
    {
        if (target instanceof ConsoleCommandSender)
        {
            sendMessage((ConsoleCommandSender) target, message);
            return;
        }

        sendMessage((Entity) target, message);
    }

    public static void sendMessage(String message)
    {
        System.out.println("[z] " + message);
    }

    public static String concatStringArray(String[] input, int offset)
    {
        StringBuilder builder = new StringBuilder("");

        for (int i = offset; i < input.length; i++)
            builder.append(input[i] + " ");

        builder.deleteCharAt(builder.length()-1);

        return builder.toString();
    }

    public static void sendMessageAll(String message)
    {
        Bukkit.getOnlinePlayers().forEach(player -> {
            sendMessage((Entity) player, message);
        });
    }

    public static void sendTextComponent(Player player, TextComponent... components)
    {
        ComponentBuilder builder = new ComponentBuilder();
        for (TextComponent component : components)
            builder.append(component);

        player.spigot().sendMessage(builder.create());
    }
}
