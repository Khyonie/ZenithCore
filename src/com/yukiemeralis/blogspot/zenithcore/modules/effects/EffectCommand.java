package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.DataUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EffectCommand extends ZenithCommand
{
    public EffectCommand() 
    {
        super("zeneffect");

        linkCommandDescription("apply <target> <effect name> <ticks>", "Applies a custom effect to a player.", AccountType.ADMIN);
        linkCommandDescription("list", "Lists all potion effects applied to you.", AccountType.USER);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            PrintUtils.sendMessage(sender, "Zenith effects module version " + ZenithCore.getModuleByName("Effects").getVersion() + ". See more info in the \"/zen mods\" menu.");
            return true;
        }

        Player target;
        ZenithEffect effect;

        switch (args[0])
        {
            case "apply":
                if (!checkAuthorization(sender, AccountType.ADMIN))
                    return true;

                if (args.length < 4)
                {
                    PrintUtils.sendMessage(sender, "Usage: /zeneffect apply <target> <effect name> <ticks>");
                    return true;
                }

                target = Bukkit.getPlayerExact(args[1]);

                if (target == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: There isn't a player named \"" + args[1] + "\" online!");
                    return true;
                }

                effect = (ZenithEffect) DataUtils.searchAndMatchClass(args[2], ZenithEffect.class);

                if (effect == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Could not find Zenith potion effect named \"" + args[2] + "\"!");
                    return true;
                }

                try {
                    Integer.valueOf(args[3]);
                } catch (NumberFormatException e) {
                    PrintUtils.sendMessage(sender, "Usage: /zeneffect apply <target> <effect name> <ticks>");
                    return true;
                }

                effect.setTarget(target);
                EffectManager.addEffect((LivingEntity) target, effect, Integer.valueOf(args[3]));

                PrintUtils.sendMessage(sender, "Success! Applied effect \"" + effect.getName() + "\" to target for " + (Integer.valueOf(args[3])/20.0) + " seconds.");
                return true;
            case "list":
                if (sender instanceof ConsoleCommandSender)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Console users cannot have effects applied to themselves!");
                    return true;
                }

                PrintUtils.sendMessage(sender, "--[ Active custom potion effects ]--");
                EffectManager.getActiveEffects((Player) sender).forEach(effect_ -> {
                    PrintUtils.sendMessage(sender, effect_.getName());
                });
            default:
                return true;
        }
    }
}
