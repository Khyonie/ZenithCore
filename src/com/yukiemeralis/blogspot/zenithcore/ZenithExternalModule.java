package com.yukiemeralis.blogspot.zenithcore;

public abstract class ZenithExternalModule
{
    protected String name, version = "1.0";
    protected ZenithCore instance = null;

    protected boolean readyForLoading = false;

    public ZenithExternalModule(String name, String version)
    {
        this.name = name;
        this.version = version;
    }

    public String getModuleFamilyName()
    {
        return name;
    }

    public String getModuleVersion()
    {
        return version;
    }

    public void setInstance(ZenithCore instance)
    {
        this.instance = instance;
    }

    public boolean isReadyForLoading()
    {
        return readyForLoading;
    }

    public void readyToLoad()
    {
        readyForLoading = true;
    }
}
