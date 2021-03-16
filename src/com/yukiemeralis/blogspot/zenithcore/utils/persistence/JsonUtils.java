package com.yukiemeralis.blogspot.zenithcore.utils.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils 
{
    private static GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson;
    private static Gson uglygson;

    // Paths
    public final static String basepath = "./plugins/zenithcore/";

    static 
    {
        uglygson = gsonBuilder.disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();

        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static void init()
    {
        if (!(new File(basepath).exists()))
        {
            new File(basepath).mkdirs();
        }
    }

    public static void initDir(String path)
    {
        if (!(new File(path).exists()))
        {
            new File(path).mkdirs();
        }
    }

    public static void initFile(String path)
    {
        if (!(new File(path).exists()))
        {
            try {
                new File(path).createNewFile();
            } catch (IOException e) {}
        }
    }

    public static Gson getGson()
    {
        return gson;
    }

    public static Gson getUglyGson()
    {
        return uglygson;
    }

    public static <T> Object fromJsonFile(String path, Class<T> type)
    {
        File file = new File(path);

        try {
            T obj = gson.fromJson(new FileReader(file), type);

            if (obj instanceof Deserializable)
                ((Deserializable) obj).deserialize();

            return obj;
        } catch (FileNotFoundException error) {
            return null;
        }
    }

    public static void toJsonFile(String path, Object obj)
    {
        File file = new File(path);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            if (obj instanceof Serializable)
                ((Serializable) obj).serialize();

            writer.write(gson.toJson(obj));
            writer.flush();

            writer.close();
        } catch (IOException e) {}
    }
}
