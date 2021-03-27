package com.yukiemeralis.blogspot.zenithcore.modules.administration;

import com.yukiemeralis.blogspot.zenithcore.ZenithModule;

import org.bukkit.Material;

public class AdminModule extends ZenithModule
{
    public AdminModule() 
    {
        super("ZenithAdmin", "1.0a", 2, "A collection of administrative tools.", Material.SALMON_BUCKET);

        setDetails("Yuki_emeralis (Hailey)", "31621-0.0.6a", "ZenithCore");

        addCommand(new AdminCommand());
        addEvent(new InventoryEditGUI());
    }

    @Override
    public void onEnable() 
    {
        
    }

    @Override
    public void onDisable() 
    {
        
    }
    
}
