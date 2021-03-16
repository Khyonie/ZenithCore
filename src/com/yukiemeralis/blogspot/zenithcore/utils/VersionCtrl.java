package com.yukiemeralis.blogspot.zenithcore.utils;

import java.io.File;

public class VersionCtrl 
{
    private static String version = null;  
    
    public static String getVersion()
    {
        if (version == null)
        {
            File file = getPluginJar();

            if (file == null)
            {
                version = "unknown";
                return "unknown";
            }

            version = file.getName().split("ZenithCore-")[1].split(".jar")[0];
            return version;
        }

        return version;
    }

    private static File getPluginJar()
    {
        for (File f : new File("./plugins").listFiles())
        {
            if (f.getName().contains("ZenithCore-"))
                return f;
        }

        PrintUtils.sendMessage("VersionControl could not find the plugin! Version set to \"unknown\". Consider renaming the .JAR file to start with \"ZenithCore-\".");
        return null;
    }
}
