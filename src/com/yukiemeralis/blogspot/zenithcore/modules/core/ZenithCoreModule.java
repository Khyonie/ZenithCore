package com.yukiemeralis.blogspot.zenithcore.modules.core;

import com.yukiemeralis.blogspot.zenithcore.ZenithModule;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.TabCompleteListener;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.ZenithListener;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;

import org.bukkit.Material;

public class ZenithCoreModule extends ZenithModule
{
    public ZenithCoreModule() 
    {
        super("Zenith", VersionCtrl.getVersion(), 0, "Core zenith module.", Material.ENDER_EYE);

        setDetails("Yuki_emeralis (Hailey)", "21621-0.0.1a", "ZenithCore");

        addCommand(new ZenithCoreCommand());
        addEvent(new ZenithListener());
        addEvent(new TabCompleteListener());
        addEvent(new ZenithModsGUI());
        addEvent(new ZenithModsGUI.ZenithModInfo());
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
