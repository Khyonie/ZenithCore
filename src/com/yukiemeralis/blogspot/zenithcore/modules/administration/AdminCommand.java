package com.yukiemeralis.blogspot.zenithcore.modules.administration;

import java.util.ArrayList;
import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.PermissionManager;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AdminCommand extends ZenithCommand
{
    public AdminCommand() 
    {
        super("zenadmin");

        linkCommandDescription("", "Display information about this module.");
        linkCommandDescription("invedit <player>", "Edit a player's inventory.", AccountType.ADMIN);
        linkCommandDescription("ecedit <player>", "Edit a player's ender chest.", AccountType.ADMIN);
        linkCommandDescription("tpspawn <player>", "Teleport a player to their spawnpoint.", AccountType.ADMIN);
        linkCommandDescription("getloc <player>", "Get the current location of a player.", AccountType.ADMIN);
        linkCommandDescription("showraid", "Highlight all raid entities for 10 minutes.", AccountType.ADMIN);
        linkCommandDescription("god <player> <ticks>", "Give a player invincibility for <ticks> ticks.", AccountType.ADMIN);

        linkCommandDescription("zap <player>", "Strike a player with lightning.", AccountType.ADMIN);
        linkCommandDescription("launch <player> <velocity>", "Launch a player into the air.", AccountType.ADMIN);
        linkCommandDescription("smash <player> <velocity>", "Launch a player downwards.", AccountType.ADMIN);
        linkCommandDescription("ignite <player> <ticks>", "Set a player on fire.", AccountType.ADMIN);
        linkCommandDescription("void <player>", "Banish a player to the void. Works well with /zenadmin god.", AccountType.ADMIN);
        linkCommandDescription("megazombie <player> <ticks>", "Send a powerful, invincible zombie to attack a player. Despawns after a period of time.", AccountType.ADMIN);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) 
    {
        // No authentication

        if (args.length == 0)
        {
            PrintUtils.sendMessage(sender, "ZenithAdministration module v" + ZenithCore.getModuleByName("ZenithAdmin").getVersion() + ".");
            return true;
        }

        switch (PermissionManager.isAuthorized(sender, AccountType.ADMIN).name())
        {
            case "REJECTED_NO_AUTH":
                PrintUtils.sendMessage(sender, "ERROR: A higher level of permission to perform this command.");
                return true;
            case "REJECTED_NO_ACCT":
                PrintUtils.sendMessage(sender, "ERROR: This command requires authentication. Please log in.");
                return true;
            case "REJECTED_UNKNOWN":
                PrintUtils.sendMessage(sender, "ERROR: An unknown error occurred. Instance has been logged.");
                PrintUtils.sendMessage("ERROR: An unknown error occurred while user " + sender.getName() + " attempted to perform the above command.", InfoType.ERROR);
                return true;
            default:
                break;
        }

        // Authorized

        switch (args[0])
        {
            case "invedit":
                if (checkIsConsole(sender))
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command cannot be run by the console.");
                    return true;
                }

                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_invedit((Player) sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "ecedit":
                if (checkIsConsole(sender))
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command cannot be run by the console.");
                    return true;
                }

                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }
                
                cmd_ecedit((Player) sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "tpspawn":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_tpspawn(sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "getloc":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_loc(sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "showraid":
                cmd_showraid(sender);
                return true;
            case "god":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (args.length == 2)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a time (in ticks) to be specified.");
                    return true;
                }

                try {
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Expected integer, got string: \"" + args[2] + "\".");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_god(sender, Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
                return true;
            case "zap":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_zap(sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "launch":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (args.length == 2)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a velocity (as float) to be specified.");
                    return true;
                }

                try {
                    Float.parseFloat(args[2]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Expected float, got string: \"" + args[2] + "\".");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_launch(sender, Bukkit.getPlayerExact(args[1]), Float.parseFloat(args[2]));
                return true;
            case "smash":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (args.length == 2)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a velocity (as float) to be specified.");
                    return true;
                }

                try {
                    Float.parseFloat(args[2]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Expected float, got string: \"" + args[2] + "\".");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_smash(sender, Bukkit.getPlayerExact(args[1]), Float.parseFloat(args[2]));
                return true;
            case "ignite":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (args.length == 2)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a time (in ticks) to be specified.");
                    return true;
                }

                try {
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Expected integer, got string: \"" + args[2] + "\".");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_ignite(sender, Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
                return true;
            case "void":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }
                
                cmd_void(sender, Bukkit.getPlayerExact(args[1]));
                return true;
            case "megazombie":
                // Args check
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a target player to be specified.");
                    return true;
                }

                if (args.length == 2)
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires a time (in ticks) to be specified.");
                    return true;
                }

                try {
                    Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Expected integer, got string: \"" + args[2] + "\".");
                    return true;
                }

                if (Bukkit.getPlayerExact(args[1]) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                cmd_megazombie(sender, Bukkit.getPlayerExact(args[1]), Integer.parseInt(args[2]));
                return true;
            default:
                PrintUtils.sendMessage(sender, "No administration command named \"" + args[0] + "\" exists!");
                return true;
        }
    }

    private boolean checkIsConsole(CommandSender sender)
    {
        return (sender instanceof ConsoleCommandSender);
    }
    
    // Command methods

    private void cmd_invedit(Player sender, Player target)
    {
        InventoryEditGUI gui = new InventoryEditGUI(target, false);
        gui.init();
        gui.display(sender);
    }

    private void cmd_ecedit(Player sender, Player target)
    {
        InventoryEditGUI gui = new InventoryEditGUI(target, true);
        gui.init();
        gui.display(sender);
    }

    private void cmd_tpspawn(CommandSender sender, Player target)
    {
        PrintUtils.sendMessage(sender, "Returning " + target.getDisplayName() + " to their spawnpoint...");
        target.teleport(target.getBedSpawnLocation());
        PrintUtils.sendMessage(target, "You've been returned to your spawnpoint.");
    }

    private void cmd_loc(CommandSender sender, Player target)
    {
        Location loc = target.getLocation();

        PrintUtils.sendMessage(sender, target.getDisplayName() + " location:");
        PrintUtils.sendMessage(sender, "Coordinates:" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
        PrintUtils.sendMessage(sender, "World: " + loc.getWorld().getName());
    }

    @SuppressWarnings("serial")
    private static final List<EntityType> raid_types = new ArrayList<>() {{
        add(EntityType.PILLAGER);
        add(EntityType.VINDICATOR);
        add(EntityType.RAVAGER);
        add(EntityType.WITCH);
        add(EntityType.EVOKER);
        add(EntityType.VEX);
    }};

    private void cmd_showraid(CommandSender sender)
    {
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 600*20, 0);

        Bukkit.getWorld("world").getEntities().forEach(ent -> {
            if (raid_types.contains(ent.getType()))
            {
                ((LivingEntity) ent).addPotionEffect(effect);
            }
        });

        PrintUtils.sendMessage(sender, "Success! Applied glowing to all raid-type entities.");
    }

    private void cmd_god(CommandSender sender, Player target, int ticks)
    {
        PotionEffect effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ticks, 4);
        ((LivingEntity) target).addPotionEffect(effect);

        PrintUtils.sendMessage(target, "You've been given invincibility!");
        PrintUtils.sendMessage(sender, "Success! Applied invincibility to " + target.getDisplayName() + " for " + ticks + " ticks.");
    }

    // Punishment commands

    private void cmd_zap(CommandSender sender, Player target)
    {
        PrintUtils.sendMessage(sender, target.getDisplayName() + " has been struck by lightning.");
        target.getWorld().strikeLightning(target.getLocation());
        PrintUtils.sendMessage(target, "You have incurred the wrath of the heavens!");
    }

    private void cmd_launch(CommandSender sender, Player target, float velocity)
    {
        PrintUtils.sendMessage(sender, target.getDisplayName() + " has been launched into the air with a velocity of " + velocity + ".");
        target.setVelocity(new Vector(0, velocity, 0));
        PrintUtils.sendMessage(target, "Nyoom!");
    }

    private void cmd_smash(CommandSender sender, Player target, float velocity)
    {
        PrintUtils.sendMessage(sender, target.getDisplayName() + " has been launched downwards with a velocity of " + velocity + ".");
        target.setVelocity(new Vector(0, velocity, 0).multiply(-1));
    }

    private void cmd_ignite(CommandSender sender, Player target, int ticks)
    {
        PrintUtils.sendMessage(sender, target.getDisplayName() + " has been set on fire for " + ticks + " ticks.");
        target.setFireTicks(ticks);
        PrintUtils.sendMessage(target, "You've been set on fire!");
    }

    private void cmd_void(CommandSender sender, Player target)
    {
        PrintUtils.sendMessage(sender, target.getDisplayName() + " has been set banished to the void.");

        Location targetloc = target.getLocation().clone();
        targetloc.setY(-100);
        target.teleport(targetloc);
    }

    private void cmd_megazombie(CommandSender sender, Player target, int ticks)
    {
        Zombie zombie = (Zombie) target.getWorld().spawnEntity(target.getLocation(), EntityType.ZOMBIE);

        // Potion effects
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, ticks, 1);
        PotionEffect fireres = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, ticks, 0);

        // Enchanted sword
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 20);
        sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 6);

        // Set the zombie's properties
        zombie.setCustomName("Dave");
        zombie.setCustomNameVisible(true);
        ((CraftEntity) zombie).getHandle().setInvulnerable(true);

        // Add armor and weapon
        ((LivingEntity) zombie).getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        ((LivingEntity) zombie).getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        ((LivingEntity) zombie).getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        ((LivingEntity) zombie).getEquipment().setItem(EquipmentSlot.HAND, sword);
        ((LivingEntity) zombie).getEquipment().setItem(EquipmentSlot.OFF_HAND, new ItemStack(Material.SHIELD));

        // Apply potion effects
        ((LivingEntity) zombie).addPotionEffect(speed);
        ((LivingEntity) zombie).addPotionEffect(fireres);

        zombie.setTarget(target);

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                PrintUtils.sendMessage(sender, "Dave has despawned.");
                zombie.remove();
            }
        }.runTaskLater(ZenithCore.getInstance(), ticks);
    }
}
