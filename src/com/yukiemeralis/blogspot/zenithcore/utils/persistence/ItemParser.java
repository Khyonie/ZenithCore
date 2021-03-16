package com.yukiemeralis.blogspot.zenithcore.utils.persistence;

import org.bukkit.inventory.ItemStack;

public interface ItemParser<T> 
{
    /**
     * Write an object's data to an ItemStack's PersistentDataContainer
     * @param target The itemstack to save to
     * @param object The object to save
     */
    public void write(ItemStack target, T object);

    /**
     * Read data off of an ItemStack's PersistentDataContainer<p>
     * Returns null if no data is read or the data is incomplete.
     * @param target THe itemstack to read from
     * @return The data stored on a PersistentDataContainer
     */
    public T read(ItemStack target);
}
