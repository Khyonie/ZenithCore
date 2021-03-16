package com.yukiemeralis.blogspot.zenithcore.modules.npc;

import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.ZenithModule;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class NpcModule extends ZenithModule
{
    public NpcModule() 
    {
        super("NPCs", "1.0", 1, "NPC management module.", Material.PLAYER_HEAD);

        setDetails("Yuki_emeralis (Hailey)", "22321-0.0.3a", "ZenithCore");

        addCommand(new NpcCommand());
    }

    @Override
    public void onEnable() 
    {
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    NpcManager.getNpcs().forEach((name, npc) -> {
                        //npc.update(player);
                        //PacketUtils.hideEntity(npc.getHost(), player);
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
