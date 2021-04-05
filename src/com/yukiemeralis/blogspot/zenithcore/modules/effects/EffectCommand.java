package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.PermissionManager;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.command.CommandSender;

public class EffectCommand extends ZenithCommand
{
    public EffectCommand() 
    {
        super("zeneffect");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        switch (PermissionManager.isAuthorized(sender, AccountType.ADMIN))
        {
            case ACCEPTED:
                break;
            case REJECTED_NO_ACCT:
                PrintUtils.sendMessage(sender, "ERROR: This command requires authentication. Please log in.");
                return true;
            case REJECTED_NO_AUTH:
                PrintUtils.sendMessage(sender, "ERROR: A higher level of permission to perform this command.");
                return true;
            default:
                PrintUtils.sendMessage(sender, "ERROR: An unknown error occurred. Instance has been logged.");
                PrintUtils.sendMessage("ERROR: An unknown error occurred while user " + sender.getName() + " attempted to perform the above command.", InfoType.ERROR);
                return true;
        }

        if (args.length == 0)
        {
            PrintUtils.sendMessage(sender, "Zenith effects module version " + ZenithCore.getModuleByName("Effects").getVersion() + ". See more info in the \"/zen mods\" menu.");
            return true;
        }

        return true;
    }
    
}
