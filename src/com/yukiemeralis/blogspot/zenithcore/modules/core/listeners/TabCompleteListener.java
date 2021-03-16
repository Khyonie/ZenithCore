package com.yukiemeralis.blogspot.zenithcore.modules.core.listeners;

import java.util.ArrayList;
import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class TabCompleteListener implements Listener
{
    @EventHandler
    public void onTabComplete(TabCompleteEvent event)
    {
        ZenithCommand target_command = null;

        for (ZenithCommand command : ZenithCore.getCommands())
        {
            if (command.getName().startsWith(event.getBuffer()))
            {
                target_command = command;
                break;
            }
        }

        if (target_command == null)
            return;

        List<String> suggestions = new ArrayList<String>();

        suggestions.addAll(target_command.getCommandDescriptions().keySet());
        event.getCompletions().addAll(suggestions);
    }
}
