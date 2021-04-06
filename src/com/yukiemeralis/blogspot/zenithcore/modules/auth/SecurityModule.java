package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.ZenithModule;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.DataUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SecurityModule extends ZenithModule
{
    private static HashMap<Player, SecurePlayerAccount> logged_in_accounts = new HashMap<>();

    private static AccountManager account_list;

    private static ArrayList<AccountRequest> requests = new ArrayList<>();

    public SecurityModule() 
    {
        super("ZenithSecurity", "1.0", 1, "Zenith user account controller.", Material.IRON_BARS);

        setDetails("Yuki_emeralis (Hailey)", "32121-0.0.8a", "ZenithCore");

        addCommand(new SecurityCommand());
    }

    @Override
    public void onEnable() 
    {
        if (!(new File(JsonUtils.basepath + "UserAccounts.json").exists()))
        {
            account_list = new AccountManager();
            account_list.getAccounts().put("console", new SecurePlayerAccount());
            JsonUtils.toJsonFile(JsonUtils.basepath + "UserAccounts.json", account_list);
        }
            
        account_list = (AccountManager) JsonUtils.fromJsonFile(JsonUtils.basepath + "UserAccounts.json", AccountManager.class);

        if (account_list == null)
        {
            PrintUtils.sendMessage("ERROR: User account list is corrupt! Continuing with a fresh instance...", InfoType.ERROR);
            File f = new File(JsonUtils.basepath + "UserAccounts.json");
            PrintUtils.sendMessage("A backup of the corrupt file has been saved to " + DataUtils.moveToLostAndFound(f).getAbsolutePath() + ".", InfoType.ERROR);
            account_list = new AccountManager();
            account_list.getAccounts().put("console", new SecurePlayerAccount());
        }

        if (!SecurePlayerAccount.comparePassword(getAccount("console"), ZenithCore.getSettings().getConsolePassword()))
        {
            // Re-register
            account_list.getAccounts().put("console", new SecurePlayerAccount());
        }
    }

    @Override
    public void onDisable() 
    {
        logoutAll();
        JsonUtils.toJsonFile(JsonUtils.basepath + "UserAccounts.json", account_list);
    }

    // 
    // Static methods 
    //

    public static SecurePlayerAccount getAccount(String username)
    {
        return account_list.getAccounts().get(username);
    }

    public static void loginAccount(Player player, SecurePlayerAccount account)
    {
        logged_in_accounts.put(player, account);
    }

    public static void logoutAccount(Player player)
    {
        logged_in_accounts.remove(player);
    }

    public static boolean isLoggedIn(Player player)
    {
        return logged_in_accounts.containsKey(player);
    }

    public static boolean isAuthenticated(Player player)
    {
        return logged_in_accounts.containsKey(player);
    }

    public static void logoutAll()
    {
        logged_in_accounts.clear();
    }

    public static Set<Player> getLoggedInUsers()
    {
        return logged_in_accounts.keySet();
    }

    public static SecurePlayerAccount getLoggedInAccount(Player player)
    {
        return logged_in_accounts.get(player);
    }

    public static List<AccountRequest> getAccountRequests()
    {
        return requests;
    }

    public static AccountRequest getRequest(String username)
    {
        for (AccountRequest req : requests)
        {
            if (req.getUsername().equals(username))
                return req;
        }

        return null;
    }

    public static boolean rejectRequest(String username)
    {
        for (AccountRequest req : requests)
        {
            if (req.getUsername().equals(username))
            {
                requests.remove(req);
                return true;
            }
        }

        return false;
    }

    public static void addAccount(SecurePlayerAccount account)
    {
        account_list.getAccounts().put(account.getUsername(), account);
    }
}
