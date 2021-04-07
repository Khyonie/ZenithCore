package com.yukiemeralis.blogspot.zenithcore.utils.persistence;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.UUID;

import com.google.common.io.Files;
import com.yukiemeralis.blogspot.zenithcore.ZenithCore;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class DataUtils 
{
    public static Object fromClassName(String package_, String className)
    {
        try {
            Class<?> class_ = Class.forName(package_ + "." + className);
            Constructor<?> constructor = class_.getConstructor((Class<?>[]) new Class[0]);

            return constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?> getClassFrom(String package_, String className)
    {
        try {
            return Class.forName(package_ + "." + className);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveToNamespacedKey(ItemStack target, String key, int offset, String value)
    {
        NamespacedKey nskey = new NamespacedKey((Plugin) ZenithCore.getInstance(), (key + offset));

        ItemMeta meta = target.getItemMeta();
        meta.getPersistentDataContainer().set(nskey, PersistentDataType.STRING, value);

        target.setItemMeta(meta);
    }

    public static void saveToNamespacedKey(ItemStack target, String key, String value)
    {
        NamespacedKey nskey = new NamespacedKey((Plugin) ZenithCore.getInstance(), key);

        ItemMeta meta = target.getItemMeta();
        meta.getPersistentDataContainer().set(nskey, PersistentDataType.STRING, value);

        target.setItemMeta(meta);
    }

    public static String readFromNamespacedKey(ItemStack target, String key)
    {
        NamespacedKey nskey = new NamespacedKey((Plugin) ZenithCore.getInstance(), key);

        ItemMeta meta = target.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(nskey, PersistentDataType.STRING))
        {
            return container.get(nskey, PersistentDataType.STRING);
        } else {
            return null;
        }
    }

    public static File moveToLostAndFound(File file)
    {
        // Find an adequate name
        final String name = file.getName();
        String nameBuffer = name + "0";
        
        File lostnfound = new File(JsonUtils.basepath + "lostandfound/");
        
        if (!lostnfound.exists())
            lostnfound.mkdirs();

        // Make sure we don't overwrite existing files
        int value = 0;
        while (new File(JsonUtils.basepath + "lostandfound/" + nameBuffer).exists())
        {
            value++;
            nameBuffer = name + value;
        }

        try {
            Files.copy(file, new File(JsonUtils.basepath + "lostandfound/" + nameBuffer));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(JsonUtils.basepath + "lostandfound/" + nameBuffer);
    }

    /**
     * Applies a UUID to an itemstack, preventing it from stacking with other like itemstacks.
     * @param target
     */
    public static void applyUUID(ItemStack target)
    {
        saveToNamespacedKey(target, "uuid", UUID.randomUUID().toString());
    }
}
