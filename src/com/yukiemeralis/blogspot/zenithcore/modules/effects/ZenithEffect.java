package com.yukiemeralis.blogspot.zenithcore.modules.effects;

import org.bukkit.entity.LivingEntity;

public abstract class ZenithEffect 
{
    protected String name;
    protected LivingEntity target;

    public ZenithEffect(String name)
    {
        this.name = name;
    }

    public void setTarget(LivingEntity target)
    {
        this.target = target;
    }

    public String getName()
    {
        return name;
    }

    public abstract void run();
}
