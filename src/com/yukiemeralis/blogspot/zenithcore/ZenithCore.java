package com.yukiemeralis.blogspot.zenithcore;

import java.io.File;
import java.util.ArrayList;

import com.yukiemeralis.blogspot.zenithcore.command.CommandManager;
import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;
import com.yukiemeralis.blogspot.zenithcore.utils.VersionCtrl;
import com.yukiemeralis.blogspot.zenithcore.utils.http.ProfileManager;
import com.yukiemeralis.blogspot.zenithcore.utils.persistence.JsonUtils;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ZenithCore extends JavaPlugin
{
    private static ZenithCore INSTANCE;

    private static ArrayList<ZenithCommand> commands = new ArrayList<>();
    private static ArrayList<Listener> events = new ArrayList<>();
    private static ArrayList<ZenithModule> modules = new ArrayList<>();
    private static ArrayList<ZenithExternalModule> externalModules = new ArrayList<>();

    public static ArrayList<ZenithModule> moduleQueue = new ArrayList<>();

    private static ProfileManager profileManager;

    private static long time;

    @Override
    public void onEnable()
    {
        PrintUtils.sendMessage("");
        PrintUtils.sendMessage("---------------===<[ ZenithCore ]>===---------------");

        INSTANCE = this;
        time = System.currentTimeMillis();

        // Initialize zenithcore folder
        JsonUtils.init();

        PrintUtils.sendMessage("Pre-loading ZenithCore " + VersionCtrl.getVersion());

        // Load profiles from cache
        if (!(new File(JsonUtils.basepath + "skinprofiles.json").exists()))
        {
            JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", new ProfileManager());
        }
        profileManager = (ProfileManager) JsonUtils.fromJsonFile(JsonUtils.basepath + "skinprofiles.json", ProfileManager.class);
        PrintUtils.sendMessage("Loaded " + profileManager.getAllProfiles().size() + " profiles from cache!");

        PrintUtils.sendMessage("Finished preloading...");

        triggerPostLoadThread();
    }

    static int expectedModules = 0;

    private static void triggerPostLoadThread()
    {
        // Count expected modules
        for (File f : new File("./plugins/").listFiles())
        {
            if (f.getName().startsWith("Zenith") && !f.getName().startsWith("ZenithCore"))
                expectedModules++;
        }

        new BukkitRunnable()
        {
            @Override
            public void run() 
            {
                boolean ready = true;

                for (ZenithExternalModule mod : externalModules)
                {
                    if (!mod.isReadyForLoading())
                        ready = false;
                }

                if (ready)
                {
                    cancel();
                    postLoad();
                }
            }
        }.runTaskTimer(getInstance(), 0, 1);
    }

    private static void postLoad()
    {
        PrintUtils.sendMessage("Beginning postload...");
        PrintUtils.sendMessage("Loading an expected " + (expectedModules + 1) + " parent module(s)...");

        externalModules.forEach(mod -> {
            mod.setInstance(INSTANCE);
            PrintUtils.sendMessage("Loaded parent module: \"" + mod.getModuleFamilyName() + "\".");
        });

        // Load core modules into memory
        ModuleManager.gatherModules().forEach(mod -> {
            addModule(mod);
        });

        // Sort by priority
        modules.sort(new ZenithModule.Sorter().reversed());

        // And register it all
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

        PrintUtils.sendMessage("Finished postload! Time elapsed: " + (System.currentTimeMillis() - time) + " ms.");
        PrintUtils.sendMessage("[Loaded " + (externalModules.size() + 1) + " parent module(s) | Loaded " + modules.size() + " child module(s)]");
        PrintUtils.sendMessage("---------------===<[ ZenithCore ]>===---------------");
    }

    @Override
    public void onDisable()
    {
        modules.forEach(module -> {
            module.onDisable();
        });

        // Save cached profiles
        JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", profileManager);
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
