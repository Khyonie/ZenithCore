package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener
{
    @EventHandler
    public void entityDeath(EntityDeathEvent event)
    {
        if (EffectManager.getAllActiveEffects().containsKey(event.getEntity()))
            EffectManager.removeAllEffects(event.getEntity());
    }
}
