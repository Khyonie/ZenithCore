package com.yukiemeralis.blogspot.zenithcore.modules.npc;

import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.modules.npc.base.ZenithNPC;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.http.HttpManager;
import com.yukiemeralis.blogspot.zenithcore.utils.http.PlayerProfile;
import com.yukiemeralis.blogspot.zenithcore.utils.http.SkinnedPlayerProfile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NpcCommand extends ZenithCommand 
{
    public NpcCommand() 
    {
        super("npc");

        linkCommandDescription("", "Display information about this module.", AccountType.ADMIN);
        linkCommandDescription("create <name> <profilename>", "Create a new NPC with a name. Names must be unique.", AccountType.ADMIN);
        linkCommandDescription("spawn <name>", "Spawn a loaded NPC into the world where you are looking.", AccountType.ADMIN);
        linkCommandDescription("setbehavior <name>", "Set an NPC's behaviour and pathfinding.", AccountType.ADMIN);
        linkCommandDescription("info <name>", "Display information about an NPC.", AccountType.ADMIN);
        linkCommandDescription("loadprofile <username>", "Load and cache a user's profile.", AccountType.ADMIN);
        linkCommandDescription("delprofile <username>", "Delete a user's profile from cache. Removes all profile association with NPCs with the given profile.", AccountType.ADMIN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean execute(CommandSender sender, String commandLabel, String[] args) 
    {
        if (args.length == 0)
        {
            PrintUtils.sendMessage(sender, "ZenithNPC manager command. | " + NpcManager.getNpcs().size() + " available NPCs, " + ZenithCore.getProfileManager().getAllProfiles().size() + " available user profiles.");
            return true;
        }

        if (!checkAuthorization(sender, AccountType.ADMIN))
            return true;

        String subcmd = args[0];

        // Per-command variables
        long systime;
        PlayerProfile playerProfile;
        SkinnedPlayerProfile skinProfile;
        ZenithNPC npc;

        switch (subcmd)
        {
            case "loadprofile":
                if (args.length == 1)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Please specify a username.");
                    return true;
                }
                systime = System.currentTimeMillis();
                playerProfile = HttpManager.getUserProfile(args[1]);

                if (playerProfile == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Could not find profile of user \"" + args[1] + "\"!");
                    return true;
                }

                if (ZenithCore.getProfileManager().getAllProfiles().containsKey(playerProfile.getUsername()))
                {
                    PrintUtils.sendMessage(sender, "WARN: Profile already loaded! Using cached version...");
                    PrintUtils.sendMessage(sender, "Success! Pulled profile from cache. If it wasn't already loaded, it is now.");
                    return true;
                }

                if (HttpManager.getSkinnedUserProfile(playerProfile) == null) // Load profile into memory
                {
                    PrintUtils.sendMessage(sender, "ERROR: Please wait at least one minute before requesting a duplicate profile.");
                    return true;
                }
                PrintUtils.sendMessage(sender, "Success! Pulled profile from Mojang web API! Request took " + (System.currentTimeMillis() - systime) + " ms.");
                return true;
            case "delprofile":
                if (ZenithCore.getProfileManager().containsProfile(args[1]))
                {
                    ZenithCore.getProfileManager().removeProfile(args[1]);
                    PrintUtils.sendMessage(sender, "Success! Removed profile of " + args[1] + " from profile cache.");
                    return true;
                }

                PrintUtils.sendMessage(sender, "ERROR: Profile \"" + args[1] + "\" doesn't exist. If you're certain it does, execute /npc loadprofile " + args[1] + " to load it into memory.");
                return true;
            case "new":
                if (args.length < 3)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Usage: /npc new <name> <profilename>");
                    return true;
                }

                skinProfile = ZenithCore.getProfileManager().getProfile(args[2]);

                if (skinProfile == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Could not find profile \"" + args[2] + "\". Load/download it using /npc loadprofile " + args[2]);
                    return true;
                }
                npc = NpcManager.createNPC(args[1], skinProfile, ((Player) sender).getTargetBlock(null, 15).getLocation().add(0, 1, 0), false);
                PrintUtils.sendMessage(sender, "Success! Created a new NPC and loaded it into memory. Spawn it using /npc spawn " + args[1]);
                return true;
            case "spawn":
                npc = NpcManager.getNpcs().get(args[1]);

                if (npc == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Unknown NPC: " + args[1]);
                    return true;
                }
                
                NpcManager.spawnNPC(npc, (List<Player>) Bukkit.getOnlinePlayers());
                return true;
            default:
                return true;
        }
    }
}
