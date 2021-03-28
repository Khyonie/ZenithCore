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
import org.bukkit.scheduler.BukkitRunnable;

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

        PrintUtils.sendMessage("");
        PrintUtils.sendMessage("§e---------------§6===§c<[ §dZenithCore §c]>§6===§e---------------");
        
        time = System.currentTimeMillis();

        // Initialize zenithcore folder
        JsonUtils.init();

        PrintUtils.sendMessage("Pre-loading ZenithCore " + VersionCtrl.getVersion(), InfoType.INFO);

        // Load profiles from cache
        if (!(new File(JsonUtils.basepath + "skinprofiles.json").exists()))
        {
            JsonUtils.toJsonFile(JsonUtils.basepath + "skinprofiles.json", new ProfileManager());
        }

        profileManager = (ProfileManager) JsonUtils.fromJsonFile(JsonUtils.basepath + "skinprofiles.json", ProfileManager.class);

        try {
            PrintUtils.sendMessage("Loaded (" + profileManager.getAllProfiles().size() + ") profile(s) from cache!", InfoType.INFO);
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
            PrintUtils.sendMessage("ERROR: Settings file is corrupt! Continuing with a fresh instance...");
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
        try {
            loadModulesFromFile();
        } catch (IOException e) { e.printStackTrace(); }

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

        // Finalize preloading
        PrintUtils.sendMessage("Finished preloading...", InfoType.INFO);

        //triggerPostLoadThread();
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
        PrintUtils.sendMessage("Beginning postload...", InfoType.INFO);
        PrintUtils.sendMessage("Loading an expected (" + (expectedModules + 1) + ") parent module(s)...", InfoType.INFO);

        externalModules.forEach(mod -> {
            mod.setInstance(INSTANCE);
            PrintUtils.sendMessage("Loaded parent module: \"" + mod.getModuleFamilyName() + "\".", InfoType.INFO);
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

        PrintUtils.sendMessage("Finished postload! Time elapsed: " + (System.currentTimeMillis() - time) + " ms.", InfoType.INFO);
        PrintUtils.sendMessage("[Loaded " + (externalModules.size() + 1) + " parent module(s) | Loaded " + modules.size() + " child module(s)]", InfoType.INFO);
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
                /**
                // Attempt to pull modinfo from file
                JarFile jar = new JarFile(f.getAbsolutePath());
                Scanner scanner = new Scanner(jar.getInputStream(jar.getEntry("modinfo")));

                String classpath = scanner.nextLine();

                scanner.close();
                */

                ModuleClassLoader.loadFromJar(f).forEach(class_ -> {
                    if (class_ instanceof ZenithModule)
                    {   
                        PrintUtils.sendMessage("Found external zenith module: " + class_.getClass().getName());
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
