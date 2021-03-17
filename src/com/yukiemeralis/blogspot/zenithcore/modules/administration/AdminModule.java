package com.yukiemeralis.blogspot.zenithcore.modules.administration;

import com.yukiemeralis.blogspot.zenithcore.ZenithModule;

import org.bukkit.Material;

public class AdminModule extends ZenithModule
{
    public AdminModule() 
    {
        super("ZenithAdmin", "1.0a", 1, "A collection of administrative tools.", Material.SALMON_BUCKET);

        addCommand(new AdminCommand());
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
