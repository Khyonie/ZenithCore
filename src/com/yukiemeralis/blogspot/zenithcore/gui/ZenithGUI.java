package com.yukiemeralis.blogspot.zenithcore.gui;

import java.util.HashMap;

import com.yukiemeralis.blogspot.zenithcore.utils.ItemUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ZenithGUI implements Listener
{
    protected HashMap<Integer, ZenithButton> buttons = new HashMap<>();
    protected Inventory inv = null;

    public ZenithGUI() {}

    public ZenithGUI(int slotcount, String name)
    {
        if (slotcount % 9 != 0)
        {
            PrintUtils.sendMessage("WARN: Inventory \"" + name + "\" has an irregular slot count! Consider changing this. (" + slotcount + " -> " + (slotcount - (slotcount % 9)) + " slots)");
            slotcount -= (slotcount % 9);
        }

        inv = Bukkit.createInventory(null, slotcount, name);
    }

    @EventHandler
    public abstract void onInteract(InventoryClickEvent event);

    public abstract void init();
    public void display(Player target)
    {
        target.closeInventory();
        target.openInventory(inv);
    }

    public void paint(ItemStack item)
    {
        for (int i = 0; i < inv.getSize(); i++)
            inv.setItem(i, item.clone());
    }

    /**
     * Add a button to the inventory.
     * @param slot The inventory slot to assign this button to.
     * @param button An instance of the button to add.
     * @deprecated Access the hashmap directly.
     */
    public void addButton(int slot, ZenithButton button)
    {
        this.buttons.put(slot, button.clone());
    }

    public final static ItemStack blank_glass;

    static {
        blank_glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        ItemUtils.applyName(blank_glass, "");
    }
}
