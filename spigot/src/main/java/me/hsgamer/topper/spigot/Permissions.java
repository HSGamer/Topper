package me.hsgamer.topper.spigot;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions {
    public static final Permission SIGN_BREAK = new Permission("topper.sign.break", PermissionDefault.OP);
    public static final Permission SIGN = new Permission("topper.sign", PermissionDefault.OP);

    private Permissions() {
        // EMPTY
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(SIGN_BREAK);
        Bukkit.getPluginManager().addPermission(SIGN);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(SIGN_BREAK);
        Bukkit.getPluginManager().removePermission(SIGN);
    }
}
