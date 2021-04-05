package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import java.util.HashMap;

import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionManager 
{
    //
    // A collection of utilities to handle permission checking and elevation
    //

    @SuppressWarnings("serial")
    private static final HashMap<AccountType, Integer> levels = new HashMap<>()
    {{
        put(AccountType.USER, 0);
        put(AccountType.ADMIN, 1);
        put(AccountType.SUPERADMIN, 2);
    }};

    public static enum PermissionResult {
        ACCEPTED (true),
        REJECTED_NO_AUTH (false),
        REJECTED_NO_ACCT (false),
        REJECTED_UNKNOWN (false)
        ;

        public final boolean allowed;

        public boolean value()
        {
            return allowed;
        }

        private PermissionResult(boolean allowed) {
            this.allowed = allowed;
        }
    }

    /**
     * Check if a user's account has appropriate authorization to access a resource.
     * @param sender The user to check. Console command senders will always have authorization.
     * @param minimum The minimum level to have authorization. Priority is user < admin < superadmin.
     * @return True if the user is allowed to access a resource, false if they aren't.
     */
    public static PermissionResult isAuthorized(CommandSender sender, AccountType minimum)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            return PermissionResult.ACCEPTED;
        }

        // Get the user's account, provided they're logged in
        SecurePlayerAccount account = (SecurePlayerAccount) SecurityModule.getLoggedInAccount((Player) sender);

        // If they're logged out, if the minimum level is a regular user, return true, otherwise return false
        if (account == null)
        {
            if (minimum.equals(AccountType.USER))
                return PermissionResult.ACCEPTED;

            return PermissionResult.REJECTED_NO_ACCT;
        }

        // Otherwise check the level of authorization as integers
        if (levels.get(account.getType()) >= levels.get(minimum))
            return PermissionResult.ACCEPTED;

        return PermissionResult.REJECTED_NO_AUTH;
    }
}
