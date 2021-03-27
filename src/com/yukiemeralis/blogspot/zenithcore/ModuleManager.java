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
     * Gather modules from com.yukiemeralis.blogspot.zenithcore.modules.
     */
    static List<ZenithModule> gatherModules()
    {
        List<ZenithModule> modules = new ArrayList<>();

        try {
            JarFile jarFile = new JarFile("./plugins/ZenithCore-" + VersionCtrl.getVersion() + ".jar");

            JarEntry entry = null;
            String packageName, className;
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                entry = entries.nextElement();

                if (entry.getName().startsWith("com"))
                {
                    className = pullClassName(entry.getName());
                    packageName = pullPackageName(entry.getName(), className);

                    // Filter results
                    if (packageName.contains("com.yukiemeralis.blogspot.zenithcore.modules") && className.endsWith("Module") && !className.equals("ZenithModule"))
                    {
                        ZenithModule module = (ZenithModule) DataUtils.fromClassName(packageName, className);
                        modules.add(module);
                    }
                }
            }

            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.sendMessage("Gathered " + modules.size() + " module(s) of family \"Core\"!", InfoType.INFO);
        return modules;
    }

    /**
     * Gather a list of zenith modules from an external .jar file.<p>
     * Zenith module packages must start with "com" or "net".
     * @param extmodule
     * @param jarFileLocation
     * @param expectedPackage
     * @return
     */
    public static List<ZenithModule> gatherModulesFromLocation(ZenithExternalModule extmodule, String jarFileLocation, String expectedPackage)
    {
        List<ZenithModule> modules = new ArrayList<>();

        try {
            JarFile jarFile = new JarFile(jarFileLocation);

            JarEntry entry = null;
            String packageName, className;
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                entry = entries.nextElement();

                if (entry.getName().startsWith("com") || entry.getName().startsWith("net"))
                {
                    className = pullClassName(entry.getName());
                    packageName = pullPackageName(entry.getName(), className);

                    // Filter results
                    if (packageName.contains(expectedPackage) && className.endsWith("Module"))
                    {
                        ZenithModule module = (ZenithModule) DataUtils.fromClassName(packageName, className);
                        modules.add(module);
                    }
                }
            }

            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.sendMessage("Gathered " + modules.size() + " module(s) of family \"" + extmodule.getModuleFamilyName() + "\"!", InfoType.INFO);
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
