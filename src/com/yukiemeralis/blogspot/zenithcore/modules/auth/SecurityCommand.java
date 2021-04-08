package com.yukiemeralis.blogspot.zenithcore.modules.auth;

import java.util.Arrays;

import com.yukiemeralis.blogspot.zenithcore.command.ZenithCommand;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.PermissionManager.PermissionResult;
import com.yukiemeralis.blogspot.zenithcore.modules.auth.SecurePlayerAccount.AccountType;
import com.yukiemeralis.blogspot.zenithcore.modules.core.ZenithCoreModule;
import com.yukiemeralis.blogspot.zenithcore.utils.InfoType;
import com.yukiemeralis.blogspot.zenithcore.utils.PrintUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SecurityCommand extends ZenithCommand
{
    public SecurityCommand() 
    {
        super("zenauth", Arrays.asList("zenithauth", "zenithsec", "zensec"));

        linkCommandDescription("login <username> <password>", "Attempt to log in to a secure account.");
        linkCommandDescription("new <user | admin | superadmin> <username> <password>", "Create a new account.");
        linkCommandDescription("sudo <password> <command...>", "Run a command as console user.", AccountType.ADMIN);
        linkCommandDescription("requests <list | approve | reject> <username>", "Perform account request administration.", AccountType.ADMIN);
        linkCommandDescription("autologin", "Toggles automatic secure account login on server join.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) 
    {
        if (args.length == 0)
        {
            PrintUtils.sendMessage(sender, "Unexpected usage: no subcommand specified!");
            return true;
        }

        SecurePlayerAccount account;
        AccountRequest request;

        switch(args[0])
        {
            case "login":
                if (args.length < 3)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Usage: /zenauth login <username> <password>");
                    return true;
                }

                if (sender instanceof ConsoleCommandSender)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Console user does not need to log in for command authentication.");
                    return true;
                }

                if (getLoggedInAccount(sender) != null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: You are currently logged in as \"" + getLoggedInAccount(sender).getUsername() + "\". Please log out first.");
                    return true;
                }

                account = SecurityModule.getAccount(args[1]);

                if (account == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: No such account: \"" + args[1] + "\"!");
                    return true;
                }

                if (!SecurePlayerAccount.comparePassword(account, args[2]))
                {
                    PrintUtils.sendMessage(sender, "ERROR: Incorrect password for user: \"" + args[1] + "\".");
                    return true;
                }

                SecurityModule.loginAccount((Player) sender, account);
                PrintUtils.sendMessage(sender, "Success! Logged in as user \"" + account.getUsername() + "\".");

                return true;
            case "logout":
                if (sender instanceof ConsoleCommandSender)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Console user cannot log out.");
                    return true;
                }

                if (getLoggedInAccount(sender) == null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: You are not logged in to an account.");
                    return true;
                }

                SecurityModule.logoutAccount((Player) sender);
                PrintUtils.sendMessage(sender, "Success! Logged out of account.");
                return true;
            case "new":
                if (args.length < 4)
                {
                    PrintUtils.sendMessage(sender, "ERROR: Usage: /zenauth new <user | admin | superadmin> <username> <password>");
                    return true;
                }

                if (SecurityModule.getAccount(args[2]) != null)
                {
                    PrintUtils.sendMessage(sender, "ERROR: An account named \"" + args[2] + "\" already exists!");
                    return true;
                }

                try {
                    AccountType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    PrintUtils.sendMessage(sender, "ERROR: Invalid account type! Acceptable values are: USER, ADMIN, SUPERADMIN.");
                    PrintUtils.sendMessage(sender, "Note - new admin and superadmin accounts must be approved by an admin or superadmin. ");
                    return true;
                }

                account = new SecurePlayerAccount(args[2], args[3], AccountType.valueOf(args[1].toUpperCase()));

                if (AccountType.valueOf(args[1].toUpperCase()).equals(AccountType.ADMIN) || AccountType.valueOf(args[1].toUpperCase()).equals(AccountType.SUPERADMIN))
                {
                    SecurityModule.getAccountRequests().add(new AccountRequest((Player) sender, account));
                    PrintUtils.sendMessage(sender, "Success! New admin/superadmin account \"" + account.getUsername() + "\" has been submitted for approval.");
                    return true;
                } 

                SecurityModule.addAccount(account);
                PrintUtils.sendMessage(sender, "Success! Registered new user account \"" + account.getUsername() + "\".");

                return true;
            case "requests":
                // No authentication

                if (!checkAuthorization(sender, AccountType.ADMIN))
                    return true;

                // Authenticated

                switch (args[1])
                {
                    case "list":
                        for (AccountRequest req : SecurityModule.getAccountRequests())
                        {
                            PrintUtils.sendMessage(
                                sender, 
                                "Request from: " + req.getPlayer().getDisplayName() + 
                                " for a(n) " + req.getAccount().getType().name() + 
                                " account named \"" + req.getUsername() + "\"."
                            );
                        }
                        break;
                    case "approve":
                        request = SecurityModule.getRequest(args[2]);
                        SecurityModule.addAccount(request.getAccount());

                        PrintUtils.sendMessage(sender, "Success! Approved account \"" + request.getUsername() + "\" for user " + request.getPlayer().getDisplayName() + ".");
                        if (request.getPlayer().isOnline())
                            PrintUtils.sendMessage(request.getPlayer(), "Your requested account \"" + request.getUsername() + "\" has been approved.");
                        break;
                    case "reject":
                        if (SecurityModule.rejectRequest(args[2]))
                        {
                            PrintUtils.sendMessage(sender, "Rejected account.");
                        } else {
                            PrintUtils.sendMessage(sender, "ERROR: Could not find account request for the username specified.");
                        }

                        break;
                    default:
                        PrintUtils.sendMessage(sender, "ERROR: Usage: /requests <list | approve | reject> <username>");
                        return true;
                }
                return true;
            case "autologin":
                if (sender instanceof ConsoleCommandSender)
                {
                    PrintUtils.sendMessage("ERROR: Console user cannot auto-login.", InfoType.ERROR);
                    return true;
                }

                if (PermissionManager.isAuthorized(sender, AccountType.ADMIN).equals(PermissionResult.REJECTED_NO_ACCT))
                {
                    PrintUtils.sendMessage(sender, "ERROR: This command requires authentication. Please log in.");
                    return true;
                }

                if (!ZenithCoreModule.getAccount((Player) sender).getAutoLogin())
                {
                    ZenithCoreModule.getAccount((Player) sender).setAutoLogin(SecurityModule.getLoggedInAccount((Player) sender).getUsername());
                    PrintUtils.sendMessage(sender, "Success! Enabled auto-login.");

                    if (PermissionManager.isAuthorized(sender, AccountType.ADMIN).value())
                    {
                        PrintUtils.sendMessage(sender, "WARN: This account is an admin or a superadmin, auto-login is discouraged.");
                    }
                } else {
                    ZenithCoreModule.getAccount((Player) sender).disableAutoLogin();
                    PrintUtils.sendMessage(sender, "Success! Disabled auto-login.");
                }
                return true;
            default:
                PrintUtils.sendMessage(sender, "No authentication command named \"" + args[0] + "\" exists!");
                return true;
        }
    }

    private static SecurePlayerAccount getLoggedInAccount(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            return SecurityModule.getAccount("console");
        }

        return SecurityModule.getLoggedInAccount((Player) sender);
    }
}
