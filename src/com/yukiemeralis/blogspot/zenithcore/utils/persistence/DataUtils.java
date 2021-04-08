package com.yukiemeralis.blogspot.zenithcore.utils.persistence;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.io.Files;
import com.yukiemeralis.blogspot.zenithcore.ZenithCore;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;

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

    public static Object instantiateClass(Class<?> class_)
    {
        try {
            return class_.getConstructor((Class<?>[]) new Class[0]).newInstance(new Object[0]);
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

    private static HashMap<String, Class<?>> cached_searches = new HashMap<>();

    public static Object searchAndMatchClass(String target, Class<?> expectedSuperclass)
    {
        // If we've cached the result, just use that
        if (cached_searches.containsKey(target))
            return instantiateClass(cached_searches.get(target));

        // I hate this, but here we go
        
        try {
            // Open the .jar resource
            JarFile jarFile = new JarFile("./plugins/ZenithCore-" + VersionCtrl.getVersion() + ".jar");
    
            JarEntry entry = null;
            String packageName, className;
            Enumeration<JarEntry> entries = jarFile.entries();
    
            // Go over every file inside the .jar
            while (entries.hasMoreElements())
            {
                entry = entries.nextElement();
    
                className = pullClassName(entry.getName());
                packageName = pullPackageName(entry.getName(), className);

                // Filter
                if (!entry.getName().endsWith(".class") || 
                    entry.isDirectory() || 
                    !className.equals(target) ||
                    !expectedSuperclass.isAssignableFrom(DataUtils.getClassFrom(packageName, className))
                )
                    continue;
    
                // Instantiate the class and return it
                jarFile.close();
                Object obj = DataUtils.fromClassName(packageName, className);

                // And cache the result, because we aren't complete savages
                cached_searches.put(target, obj.getClass());

                return obj;
            }

            jarFile.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Applies a UUID to an itemstack, preventing it from stacking with other like itemstacks.
     * @param target
     */
    public static void applyUUID(ItemStack target)
    {
        saveToNamespacedKey(target, "uuid", UUID.randomUUID().toString());
    }

    private static String pullClassName(String entryName)
    {
        String className = entryName.split("/")[entryName.split("/").length-1]
            .split(".class")[0];

        return className;
    }

    private static String pullPackageName(String entryName, String className)
    {
        String packageName = entryName.replace("/", ".")
            .replace("." + className + ".class", "");

        return packageName;
    }
}
