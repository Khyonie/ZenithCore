package com.yukiemeralis.blogspot.zenithcore.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class ZenithCommand extends Command 
{
    private HashMap<String, String> command_descriptions = new HashMap<>();

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
        command_descriptions.put(command, description);
    }

    public HashMap<String, String> getCommandDescriptions()
    {
        return command_descriptions;
    }
}
