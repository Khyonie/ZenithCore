package com.yukiemeralis.blogspot.zenithcore.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils 
{
    public static void applyName(ItemStack target, String name)
    {
        ItemMeta meta = target.getItemMeta();
        meta.setDisplayName(name);

        target.setItemMeta(meta);
    }

    public static void applyLore(ItemStack target, String... lore)
    {
        ItemMeta meta = target.getItemMeta();
        List<String> lore_list = new ArrayList<String>();

        for (String str : lore)
            lore_list.add(str);

        meta.setLore(lore_list);

        target.setItemMeta(meta);
    }

    public static void applyEnchantment(ItemStack target, Enchantment enchant, int level)
    {
        target.addUnsafeEnchantment(enchant, level);
    }

    public static void removeEnchantment(ItemStack target, Enchantment enchant)
    {
        target.removeEnchantment(enchant);
    }

    public static Enchantment enchantFromName(String name)
    {
        return Enchantment.getByKey(NamespacedKey.minecraft(name));
    }
}
