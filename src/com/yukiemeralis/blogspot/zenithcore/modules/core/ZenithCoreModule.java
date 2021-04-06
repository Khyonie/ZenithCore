package com.yukiemeralis.blogspot.zenithcore.modules.core;

import java.io.File;
import java.util.HashMap;

import com.yukiemeralis.blogspot.zenithcore.ZenithModule;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.TabCompleteListener;
import com.yukiemeralis.blogspot.zenithcore.modules.core.listeners.ZenithListener;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.DataUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ZenithCoreModule extends ZenithModule
{
    private static HashMap<Player, PlayerAccount> accounts = new HashMap<>();

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
        if (!(new File(JsonUtils.basepath + "accounts/").exists()))
            new File(JsonUtils.basepath + "accounts/").mkdirs();
    }

    @Override
    public void onDisable() 
    {

    }

    public static HashMap<Player, PlayerAccount> getAllAccounts()
    {
        return accounts;
    }
    
    public static PlayerAccount getAccount(Player player)
    {
        if (accounts.containsKey(player))
            return accounts.get(player);

        PlayerAccount account;

        File account_file = new File(JsonUtils.basepath + "accounts/" + player.getUniqueId().toString() + ".json");

        if (account_file.exists())
        {
            account = (PlayerAccount) JsonUtils.fromJsonFile(JsonUtils.basepath + "accounts/" + player.getUniqueId().toString() + ".json", PlayerAccount.class);

            if (account == null)
            {
                PrintUtils.sendMessage("ERROR: Regular user account for user " + player.getDisplayName() + " is corrupt!", InfoType.ERROR);
                PrintUtils.sendMessage("A backup of the corrupt file has been saved to " + DataUtils.moveToLostAndFound(account_file).getAbsolutePath() + ".", InfoType.ERROR);

                account = new PlayerAccount(player);
            }

            accounts.put(player, account);
            return account;
        }

        account = new PlayerAccount(player);
        accounts.put(player, account);
        return account;
    }

    public static void createAccount(Player player)
    {
        accounts.put(player, new PlayerAccount(player));
    }
}
