package com.yukiemeralis.blogspot.zenithcore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ZenithButton implements Cloneable
{
    Material icon = Material.BARRIER;
    String label = null;
    List<String> lore = null;

    public ZenithButton(Material icon)
    {
        this.icon = icon;
    }

    public ZenithButton(Material icon, String label)
    {
        this.icon = icon;
        this.label = label;
    }

    public ZenithButton(Material icon, String label, String... lore)
    {
        this.icon = icon;
        this.label = label;

        this.lore = new ArrayList<>();

        for (String str : lore)
            this.lore.add(str);
    }

    public abstract void onClick(Player player, int slot, InventoryAction action, InventoryClickEvent event);

    public ItemStack generate()
    {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();

        if (label != null)
            meta.setDisplayName(label);

        if (lore != null)
            meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ZenithButton clone()
    {
        try {
            return (ZenithButton) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
