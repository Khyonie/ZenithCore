package com.yukiemeralis.blogspot.zenithcore.modules.effects.base;

import com.yukiemeralis.blogspot.zenithcore.modules.effects.ZenithEffect;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

public class StunEffect extends ZenithEffect
{
    Location loc;
    public StunEffect() 
    {
        super("Stunned");
    }

    @Override
    public void setTarget(LivingEntity target)
    {
        this.target = target;
        this.loc = target.getLocation();
    }

    @Override
    public void run() 
    {
        ((CraftEntity) target).getHandle().setLocation(loc.getX(), loc.getY(), loc.getZ(), target.getLocation().getPitch(), target.getLocation().getYaw());
    }
}
