package com.yukiemeralis.blogspot.zenithcore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yukiemeralis.blogspot.zenithcore.command.CommandManager;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.ModuleClassLoader;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;
import com.yukiemeralis.blogspot.zenithcore.utils.http.ProfileManager;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ZenithCore extends JavaPlugin
{
    private static ZenithCore INSTANCE;

    private static ArrayList<ZenithCommand> commands = new ArrayList<>();
    private static ArrayList<Listener> events = new ArrayList<>();
    private static ArrayList<ZenithModule> modules = new ArrayList<>();
    private static ArrayList<ZenithExternalModule> externalModules = new ArrayList<>();

    private static ProfileManager profileManager;
    private static ZenithOptions zenithOptions;

    private static long time; // Track loading times

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        PrintUtils.sendMessage("§e---------------§6===§c<[ §dZenithCore §c]>§6===§e---------------");
        
        time = System.currentTimeMillis();

        // Initialize zenithcore folder
        JsonUtils.init();

        PrintUtils.sendMessage("Loading ZenithCore " + VersionCtrl.getVersion(), InfoType.INFO);

        // Load profiles from cache
        if (!(new File(JsonUtils.basepath + "skinprofiles.json").exists()))
        {
            JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", new ProfileManager());
        }

        profileManager = (ProfileManager) JsonUtils.fromJsonFile(JsonUtils.basepath + "skinprofiles.json", ProfileManager.class);

        try {
            PrintUtils.sendMessage("Loaded (" + profileManager.getAllProfiles().size() + ") profile(s) from cache.", InfoType.INFO);
        } catch (NullPointerException error) {
            PrintUtils.sendMessage("ERROR: User profile cache is corrupt! Continuing with a fresh instance...", InfoType.ERROR);
            profileManager = new ProfileManager();
        }

        // Load options from file
        if (!(new File(JsonUtils.basepath + "ZenithOptions.json").exists()))
        {
            JsonUtils.toJsonFile(JsonUtils.basepath + "ZenithOptions.json", new ZenithOptions());
        }
        
        zenithOptions = (ZenithOptions) JsonUtils.fromJsonFile(JsonUtils.basepath + "ZenithOptions.json", ZenithOptions.class);

        if (zenithOptions == null)
        {
            PrintUtils.sendMessage("ERROR: Settings file is corrupt! Continuing with a fresh instance...", InfoType.ERROR);
            zenithOptions = new ZenithOptions();
        }

        //
        // Module management
        //

        if (!(new File(JsonUtils.basepath + "mods/").exists()))
        {
            new File(JsonUtils.basepath + "mods/").mkdir();
        }

        // Gather modules

        // Internal
        modules.addAll(ModuleManager.gatherModules());

        // External
        int delta = modules.size();
        try {
            modules.addAll(loadModulesFromFile());
        } catch (IOException e) { 
            e.printStackTrace(); 
        }

        // Print potential naming conflicts
        modules.forEach(module1 -> {
            modules.forEach(module2 -> {
                if (module1 != module2 && module1.getName().equals(module2.getName()))
                    PrintUtils.sendMessage("Potential conflict: Module " + module1.getClass().getName() + " has same name as module " + module2.getClass().getName() + ".", InfoType.ERROR);
            });
        });

        PrintUtils.sendMessage("Found (" + (modules.size() - delta) + ") external modules.", InfoType.INFO);

        // And load it
        modules.sort(new ZenithModule.Sorter().reversed());

        for (ZenithModule module : modules)
        {
            commands.addAll(module.getCommands());
            events.addAll(module.getListeners());

            module.onEnable();
        }

        commands.forEach(command -> {
            CommandManager.registerCommand(command.getName(), command);
        });

        events.forEach(event -> {
            getInstance().getServer().getPluginManager().registerEvents(event, getInstance());
        });

        // Finished
        PrintUtils.sendMessage("Done! Time elapsed: " + (System.currentTimeMillis() - time) + " ms", InfoType.INFO);
        PrintUtils.sendMessage("§e---------------§6===§c<[ §dZenithCore §c]>§6===§e---------------");
    }

    @Override
    public void onDisable()
    {
        modules.forEach(module -> {
            module.onDisable();
        });

        // Save cached profiles
        JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", profileManager);

        // Save settings
        JsonUtils.toJsonFile(JsonUtils.basepath + "ZenithOptions.json", zenithOptions);
    }

    private List<ZenithModule> loadModulesFromFile() throws IOException
    {
        File path = new File(JsonUtils.basepath + "mods/");
        List<ZenithModule> buffer = new ArrayList<>();

        for (File f : path.listFiles())
        {
            if (f.getName().endsWith(".jar"))
            {
                ModuleClassLoader.loadFromJar(f, this.getClassLoader()).forEach(class_ -> {
                    if (class_ instanceof ZenithModule)
                    {
                        buffer.add((ZenithModule) class_);
                    }
                });
            }

        }

        return buffer;
    }

    public static ZenithCore getInstance()
    {
        return INSTANCE;
    }

    public static ProfileManager getProfileManager()
    {
        return profileManager;
    }

    public static ArrayList<ZenithCommand> getCommands()
    {
        return commands;
    }

    public static ArrayList<ZenithModule> getModules()
    {
        return modules;
    }

    public static void addModule(ZenithModule module)
    {
        modules.add(module);
    }

    public static ZenithModule getModuleByName(String name)
    {
        for (ZenithModule module : modules)
        {
            if (module.getName().equals(name))
                return module;
        }

        return null;
    }

    public static void loadAndRegisterModule(ZenithModule module)
    {
        commands.addAll(module.getCommands());
        events.addAll(module.getListeners());
    }

    public static ZenithOptions getSettings()
    {
        return zenithOptions;
    }

    public static void registerModule(ZenithModule module)
    {
        addModule(module);

        commands.addAll(module.getCommands());
        events.addAll(module.getListeners());

        module.onEnable();
    }

    public static void addExternalModule(ZenithExternalModule module)
    {
        externalModules.add(module);
    }
}
