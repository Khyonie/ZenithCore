package com.yukiemeralis.blogspot.zenithcore;

import com.google.gson.annotations.Expose;

public class ZenithOptions 
{
    @Expose(serialize = true, deserialize = true)
    private boolean verbose_logging = false; 
    @Expose(serialize = true, deserialize = true)
    private boolean safety_backups =  false;

    @Expose(serialize = true, deserialize = true)
    private String console_password = "5w3#t-bligh][";

    public boolean getVerboseLogging()
    {
        return verbose_logging;
    } 

    public boolean getSafetyBackups()
    {
        return safety_backups;
    }
    
    public boolean toggleVerboseLogging()
    {
        verbose_logging = !verbose_logging;
        return verbose_logging;
    }

    public boolean toggleSafetyBackups()
    {
        safety_backups = !safety_backups;
        return safety_backups;
    }

    public String getConsolePassword()
    {
        return console_password;
    }
}
