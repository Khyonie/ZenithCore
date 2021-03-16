package com.yukiemeralis.blogspot.zenithcore.modules.npc.base;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class ZenithNPCEntity extends EntityCreature
 {
    ZenithNPC guest;

    public ZenithNPCEntity(EntityTypes<? extends EntityCreature> entitytypes, Location location) 
    {
        super(entitytypes, ((CraftWorld) location.getWorld()).getHandle());
    }
    
    public void linkGuest(ZenithNPC guest)
    {
        this.guest = guest;
    }

    @Override
    protected void initPathfinder()
    {

    }
}
