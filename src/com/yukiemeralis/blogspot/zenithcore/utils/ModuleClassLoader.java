package com.yukiemeralis.blogspot.zenithcore.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleClassLoader
{
    public static List<Object> loadFromJar(File file, ClassLoader parent)
    {
        List<Object> classes = new ArrayList<>();

        try {
            JarFile jarFile = new JarFile(file.getAbsolutePath());
            Enumeration<JarEntry> entries = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + file.getAbsolutePath() + "!/")};
            URLClassLoader loader = URLClassLoader.newInstance(urls, parent);

            JarEntry entry;
            while (entries.hasMoreElements())
            {
                entry = entries.nextElement();

                if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                    continue;

                String className = entry.getName().substring(0, entry.getName().length() - String.valueOf(".class").length());
                className = className.replace('/', '.');

                try {
                    Class<?> class_ = loader.loadClass(className);

                    Constructor<?> constructor = class_.getConstructor();
                    Object object = constructor.newInstance();

                    classes.add(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            jarFile.close();

            return classes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
