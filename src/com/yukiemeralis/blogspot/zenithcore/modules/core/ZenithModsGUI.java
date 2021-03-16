package com.yukiemeralis.blogspot.zenithcore.modules.core;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.ZenithModule;
import com.yukiemeralis.blogspot.zenithcore.gui.ZenithButton;
import com.yukiemeralis.blogspot.zenithcore.gui.ZenithGUI;
import com.yukiemeralis.blogspot.zenithcore.utils.ItemUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class ZenithModsGUI extends ZenithGUI
{
    public ZenithModsGUI()
    {
        super(18, "Loaded modules");
    }

    @Override
    @EventHandler
    public void onInteract(InventoryClickEvent event)
    {
        if (!event.getView().getTitle().equals("Loaded modules"))
            return;

        event.setCancelled(true);

        if (event.getClickedInventory() == null)
            return;

        if (event.getRawSlot() == -1 || event.getRawSlot() >= event.getClickedInventory().getSize())
            return;

        ItemStack item = event.getClickedInventory().getItem(event.getRawSlot());

        if (item.getType().equals(Material.BLACK_STAINED_GLASS_PANE))
            return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (ZenithCore.getModuleByName(name) == null)
            return;

        ZenithModInfo info = new ZenithModInfo(ZenithCore.getModuleByName(name));
        info.init();
        info.display((Player) event.getWhoClicked());
    }

    @Override
    public void init() 
    {
        paint(ZenithGUI.blank_glass);

        int index = 0;
        for (ZenithModule mod : ZenithCore.getModules())
        {
            inv.setItem(index, mod.getIcon());
            index++;
        }
    }

    static class ZenithModInfo extends ZenithGUI
    {
        ZenithModule module;

        // Button declaration
        final static ZenithButton cancel = new ZenithButton(Material.RED_CONCRETE, "§r§cBack", "§r§7Return to modules list.") 
        {
            @Override
            public void onClick(Player player, int slot, InventoryAction action, InventoryClickEvent event) 
            {
                ZenithModsGUI gui = new ZenithModsGUI();
                gui.init();

                PrintUtils.sendMessage("Button clicked.");

                player.closeInventory();
                gui.display(player);
            }
        };

        protected ZenithModInfo() {}

        protected ZenithModInfo(ZenithModule module)
        {
            super(9, "Detailed info: " + module.getName());
            this.module = module;

            addButton(8, cancel.clone());
        }

        @Override
        @EventHandler
        public void onInteract(InventoryClickEvent event) 
        {
            if (!event.getView().getTitle().startsWith("Detailed info: "))
                return;

            event.setCancelled(true);

            if (this.buttons.containsKey(event.getRawSlot()))
            {
                this.buttons.get(event.getRawSlot()).onClick((Player) event.getWhoClicked(), event.getRawSlot(), event.getAction(), event);
                PrintUtils.sendMessage("Button found in slot " + event.getRawSlot());
            } else {
                System.out.println("Click from " + event.getRawSlot());
            }
                
        }

        @Override
        public void init() 
        {
            paint(ZenithGUI.blank_glass);

            buttons.forEach((slot, button) ->  {
                inv.setItem(slot, button.generate());
            });

            inv.setItem(0, module.getIcon());
            inv.setItem(1, getAuthor());
            inv.setItem(2, getSinceVersion());
            inv.setItem(3, getModuleFamily());
        }

        private ItemStack getAuthor()
        {
            ItemStack item = new ItemStack(Material.FEATHER);

            ItemUtils.applyName(item, "§r§bAuthor");

            if (module.getAuthor() == null)
            {
                ItemUtils.applyLore(item, "§r§3Made by: unknown");
                return item;
            }

            ItemUtils.applyLore(item, "§r§3Made by: " + module.getAuthor());
            return item;
        }

        private ItemStack getSinceVersion()
        {
            ItemStack item = new ItemStack(Material.BOOK);

            ItemUtils.applyName(item, "§r§bSince");

            if (module.getSinceVersion() == null)
            {
                ItemUtils.applyLore(item, "§r§3This module does not have a", "§r§3\"since\" version.");
                return item;
            }

            ItemUtils.applyLore(item, "§r§3This module has existed since", "§r§3ZenithCore " + module.getSinceVersion());
            return item;
        }

        private ItemStack getModuleFamily()
        {
            ItemStack item = new ItemStack(Material.BOOKSHELF);

            ItemUtils.applyName(item, "§r§bModule family");

            if (module.getModuleFamily() == null)
            {
                ItemUtils.applyLore(item, "§r§3Unknown");
                return item;
            }

            ItemUtils.applyLore(item, "§r§3" + module.getModuleFamily());
            return item;
        }
    }
}
