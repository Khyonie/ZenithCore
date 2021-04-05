package com.yukiemeralis.blogspot.zenithcore.modules.core;

import java.util.HashMap;

import com.yukiemeralis.blogspot.zenithcore.ZenithModule;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.TabCompleteListener;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.ZenithListener;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ZenithCoreModule extends ZenithModule
{
    private static HashMap<String, PlayerAccount> accounts = new HashMap<>();

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
        /**
        if (!(new File(JsonUtils.basepath + "RegularUserAccounts.json").exists()))
        {
            JsonUtils.toJsonFile(JsonUtils.basepath + "RegularUserAccounts.json", new HashMap<>());
        }

        accounts = (HashMap<String, PlayerAccount>) JsonUtils.fromJsonFile(JsonUtils.basepath + "RegularUserAccounts.json", HashMap.class);
        */
    }

    @Override
    public void onDisable() 
    {

    }

    public static HashMap<String, PlayerAccount> getAllAccounts()
    {
        return accounts;
    }
    
    public static PlayerAccount getAccount(Player player)
    {
        return accounts.get(player.getUniqueId().toString());
    }

    public static void createAccount(Player player)
    {
        accounts.put(player.getUniqueId().toString(), new PlayerAccount(player));
    }
}
