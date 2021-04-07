package com.yukiemeralis.blogspot.zenithcore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.DataUtils;

public class ModuleManager 
{
    /**
     * Gather internal modules
     */
    static List<ZenithModule> gatherModules()
    {
        List<ZenithModule> modules = new ArrayList<>();

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

                // Filter non-class files, directories, the base ZenithModule class, or just any other random things we don't want to instantiate
                if (!entry.getName().endsWith(".class") || 
                    entry.isDirectory() || 
                    className.equals("ZenithModule") || 
                    !ZenithModule.class.isAssignableFrom(DataUtils.getClassFrom(packageName, className))
                )
                    continue;

                // Instantiate the class and add it as a module
                modules.add((ZenithModule) DataUtils.fromClassName(packageName, className));
            }

            // Finish and close the .jar resource
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.sendMessage("Gathered " + modules.size() + " module(s) of family \"Core\".", InfoType.INFO);
        return modules;
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
