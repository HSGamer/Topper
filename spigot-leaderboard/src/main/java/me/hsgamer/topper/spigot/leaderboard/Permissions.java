package me.hsgamer.topper.spigot.leaderboard;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions {
    public static final Permission SIGN_BREAK = new Permission("topper.sign.break", PermissionDefault.OP);
    public static final Permission SIGN = new Permission("topper.sign", PermissionDefault.OP);
    public static final Permission SKULL_BREAK = new Permission("topper.skull.break", PermissionDefault.OP);
    public static final Permission SKULL = new Permission("topper.skull", PermissionDefault.OP);
    public static final Permission TOP = new Permission("topper.top", PermissionDefault.OP);
    public static final Permission RELOAD = new Permission("topper.reload", PermissionDefault.OP);

    private Permissions() {
        // EMPTY
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(SIGN_BREAK);
        Bukkit.getPluginManager().addPermission(SIGN);
        Bukkit.getPluginManager().addPermission(SKULL_BREAK);
        Bukkit.getPluginManager().addPermission(SKULL);
        Bukkit.getPluginManager().addPermission(TOP);
        Bukkit.getPluginManager().addPermission(RELOAD);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(SIGN_BREAK);
        Bukkit.getPluginManager().removePermission(SIGN);
        Bukkit.getPluginManager().removePermission(SKULL_BREAK);
        Bukkit.getPluginManager().removePermission(SKULL);
        Bukkit.getPluginManager().removePermission(TOP);
        Bukkit.getPluginManager().removePermission(RELOAD);
    }
}
