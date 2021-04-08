package com.yukiemeralis.blogspot.zenithcore.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.modules.auth.PermissionManager;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class ZenithCommand extends Command 
{
    private HashMap<String, CommandDescription> command_descriptions = new HashMap<>();

    public ZenithCommand(String name) 
    {
        super(name, "ZenithCommand: " + name, "ZenithCommand: " + name, new ArrayList<>());
    }

    public ZenithCommand(String name, List<String> aliases)
    {
        super(name, "ZenithCommand: " + name, "ZenithCommand:" + name, aliases);
    }
    
    @Override
    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    protected void linkCommandDescription(String command, String description)
    {
        command_descriptions.put(command, new CommandDescription(description));
    }

    protected void linkCommandDescription(String command, String description, AccountType access_type)
    {
        command_descriptions.put(command, new CommandDescription(description, access_type));
    }

    public HashMap<String, CommandDescription> getCommandDescriptions()
    {
        return command_descriptions;
    }

    protected boolean checkAuthorization(CommandSender sender, AccountType type)
    {
        switch (PermissionManager.isAuthorized(sender, type))
        {
            case ACCEPTED:
                return true;
            case REJECTED_NO_ACCT:
                PrintUtils.sendMessage(sender, "ERROR: This command requires authentication. Please log in.");
                return false;
            case REJECTED_NO_AUTH:
                PrintUtils.sendMessage(sender, "ERROR: A higher level of permission to perform this command.");
                return false;
            default:
                PrintUtils.sendMessage(sender, "ERROR: An unknown error occurred. Instance has been logged.");
                PrintUtils.sendMessage("ERROR: An unknown error occurred while user " + sender.getName() + " attempted to perform the above command.", InfoType.ERROR);
                return false;
        }
    }
}
