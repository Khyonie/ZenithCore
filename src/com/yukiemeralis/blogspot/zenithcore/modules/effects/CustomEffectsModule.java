package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import java.util.ConcurrentModificationException;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.ZenithModule;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomEffectsModule extends ZenithModule
{
    public CustomEffectsModule() 
    {
        super("Effects", "1.0", 6, "Handler for custom entity effects.", Material.DRAGON_BREATH);

        setDetails("Yuki_emeralis (Hailey)", "40321-0.0.9a", "ZenithCore");

        addEvent(new DeathListener());
        addCommand(new EffectCommand());
    }

    @Override
    public void onEnable() 
    {
        new BukkitRunnable() {
            @Override
            public void run() 
            {
                EffectManager.getAllActiveEffects().forEach((target, effects) -> {
                    effects.forEach(effect -> {
                        try {
                            // ZenithEffect#run() is not guarunteed to not run at the same time an entity dies and EffectManager is modified.
                            effect.run();
                        } catch (ConcurrentModificationException e) {}
                    });
                });
            }
        }.runTaskTimer(ZenithCore.getInstance(), 1L, 1L);
    }

    @Override
    public void onDisable() 
    {
        
    }
    
}
