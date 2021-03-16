package com.yukiemeralis.blogspot.zenithcore;

import java.util.ArrayList;
import java.util.Comparator;

import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class ZenithModule 
{
    private String name, version, description;
    private ArrayList<ZenithCommand> commands = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<>();
    private int priority; // Load priority. Lower numbers load earlier.

    private Material icon;
    private ItemStack itemicon;

    // Details
    private String author, sinceversion, modulefamily;

    /**
     * Method that runs when the module is enabled.
     */
    public abstract void onEnable();

    /**
     * Method that runs when the module is disabled.
     */
    public abstract void onDisable();

    /**
     * Module for Zenith. Loads right after the server finishes startup.
     * @param name The name of this module.
     * @param version The version of this module.
     * @param priority The loading priority of this module. The lower the number, the higher the priority.
     */
    public ZenithModule(String name, String version, int priority)
    {
        this.name = name;
        this.version = version;
        this.priority = priority;
    }

    public ZenithModule(String name, String version, int priority, String description, Material icon)
    {
        this.name = name;
        this.version = version;
        this.priority = priority;
        this.description = description;
        this.icon = icon;
    }

    /**
     * Gets the name of this module.
     * @return This module's name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets the version of this module.
     * @return This module's version.
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Gets the priority of this module.
     * @return The module's priority.
     */
    public int getPriority()
    {
        return this.priority;
    }

    /**
     * Returns a list of Zenith commands associated with this module.
     * @return A list of ZenithCommands.
     */
    public ArrayList<ZenithCommand> getCommands()
    {
        return this.commands;
    }

    /**
     * Returns a list of event listeners associated with this module.
     * @return A list of listeners.
     */
    public ArrayList<Listener> getListeners()
    {
        return this.listeners;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public String getSinceVersion()
    {
        return this.sinceversion;
    }

    public String getModuleFamily()
    {
        return this.modulefamily;
    }

    public void setDetails(String author, String sinceversion, String modulefamily)
    {
        this.author = author;
        this.sinceversion = sinceversion;
        this.modulefamily = modulefamily;
    }

    public ItemStack getIcon()
    {
        int commandcount = 0;
        for (ZenithCommand command : this.commands)
            commandcount += command.getCommandDescriptions().size();

        if (itemicon != null)
            return itemicon;

        if (icon == null)
        {
            ItemStack item = new ItemStack(Material.ENDER_EYE);

            ItemUtils.applyName(item, "§b" + name);
            ItemUtils.applyLore(item, 
                "§r§3This module does not have a description.",
                "§r§3Commands: " + commandcount,
                "§r§3Events: " + listeners.size(),
                "§r§3Version: " + version
            );

            itemicon = item;
            return item;
        }

        ItemStack item = new ItemStack(icon);

        ItemUtils.applyName(item, "§b" + name);
        ItemUtils.applyLore(item, 
            "§r§3" + description,
            "§r§3Commands: " + commandcount,
            "§r§3Events: " + listeners.size(),
            "§r§3Version: " + version
        );

        itemicon = item;
        return item;
    }

    /**
     * Adds a Zenith command to this module. Commands that aren't added won't be loaded.
     * @param command The command to add.
     */
    protected void addCommand(ZenithCommand command)
    {
        commands.add(command);
    }

    /**
     * Adds a bukkit listener to this module.
     * @param event The listener to add.
     */
    protected void addEvent(Listener event)
    {
        listeners.add(event);
    }

    static class Sorter implements Comparator<ZenithModule>
    {
        @Override
        public int compare(ZenithModule mod1, ZenithModule mod2) 
        {
            return ((Integer) mod2.getPriority()).compareTo(mod1.getPriority());
        }
    }
}
