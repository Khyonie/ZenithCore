package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectManager 
{
    private static HashMap<LivingEntity, List<ZenithEffect>> active_effects = new HashMap<>();

    public static void addEffect(LivingEntity target, ZenithEffect effect, long duration)
    {
        if (target.isDead())
            return;

        if (!active_effects.containsKey(target))
            active_effects.put(target, new ArrayList<>());

        active_effects.get(target).add(effect);

        new BukkitRunnable() {
            @Override
            public void run() 
            {
                removeEffect(target, effect);
            }
        }.runTaskLater(ZenithCore.getInstance(), duration);
    }

    public static HashMap<LivingEntity, List<ZenithEffect>> getAllActiveEffects()
    {
        return active_effects;
    }

    public static List<ZenithEffect> getActiveEffects(LivingEntity target)
    {
        return active_effects.get(target);
    }

    public static void removeAllEffects(LivingEntity target)
    {
        active_effects.remove(target);
    }

    public static void removeEffect(LivingEntity target, Class<? extends ZenithEffect> effect)
    {
        active_effects.get(target).removeIf(zeffect -> {
                return zeffect.getClass().equals(effect);
            }
        );
    }

    public static void removeEffect(LivingEntity target, ZenithEffect effect)
    {
        active_effects.get(target).remove(effect);
    }
}
