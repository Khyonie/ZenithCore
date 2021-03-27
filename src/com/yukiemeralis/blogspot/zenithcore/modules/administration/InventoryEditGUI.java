package com.yukiemeralis.blogspot.zenithcore.modules.administration;

import java.util.HashMap;

import com.yukiemeralis.blogspot.zenithcore.gui.ZenithButton;
import com.yukiemeralis.blogspot.zenithcore.gui.ZenithGUI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryEditGUI extends ZenithGUI
{
    private Player target;
    private static HashMap<Player, Player> target_map = new HashMap<>();
    private static HashMap<Player, Boolean> settings_map = new HashMap<>();
    private boolean enderchest = false;

    // Buttons
    private final static ZenithButton close_button = new ZenithButton(Material.RED_CONCRETE, "§r§cExit without saving")
    {
        @Override
        public void onClick(Player player, int slot, InventoryAction action, InventoryClickEvent event) 
        {
            player.closeInventory();
        }
    };

    @SuppressWarnings("serial")
    HashMap<Integer, ZenithButton> buttons = new HashMap<>() {{
        put(8, close_button.clone());
    }};

    public InventoryEditGUI() {}

    public InventoryEditGUI(Player target, boolean enderchest)
    {
        super (45, "Edit inventory");
        this.target = target;
        this.enderchest = enderchest;
    }

    @Override
    public void display(Player target)
    {
        target_map.put(target, this.target);
        settings_map.put(target, enderchest);
        target.openInventory(inv);
    }

    @Override
    @EventHandler
    public void onInteract(InventoryClickEvent event) 
    {
        if (!event.getView().getTitle().equals("Edit inventory"))
            return;

        if (settings_map.get(event.getWhoClicked()))
        {
            if (event.getRawSlot() <= 9)
                event.setCancelled(true);
        } else {
            if (event.getRawSlot() <= 9 && event.getRawSlot() >= 4)
                event.setCancelled(true);
        }

        if (this.buttons.containsKey(event.getRawSlot()))
        {
            this.buttons.get(event.getRawSlot()).onClick((Player) event.getWhoClicked(), event.getRawSlot(), event.getAction(), event);
        }
        
        if (event.getRawSlot() < 9)
            return;
    }

    @Override
    public void init() 
    {
        if (enderchest) {
            for (int i = 0; i < 9; i++)
                inv.setItem(i, ZenithGUI.blank_glass.clone());

            for (int i = 0; i < target.getEnderChest().getContents().length; i++)
            {
                if (target.getEnderChest().getContents()[i] != null)
                {
                    inv.setItem(i + 9, target.getEnderChest().getContents()[i]);
                }
            }
        } else {
            for (int i = 4; i < 9; i++)
                inv.setItem(i, ZenithGUI.blank_glass.clone());

            for (int i = 0; i < 36; i++)
            {
                if (target.getInventory().getContents()[i] != null)
                {
                    inv.setItem(i + 9, target.getInventory().getContents()[i]);
                }
            }

            for (int i = 36; i < 40; i++)
            {
                try {
                    if (target.getInventory().getContents()[i] != null)
                        inv.setItem(i - 36, target.getInventory().getContents()[i]);
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        }

        this.buttons.forEach((slot, button) -> {
            inv.setItem(slot, button.generate());
        });
    }
}
