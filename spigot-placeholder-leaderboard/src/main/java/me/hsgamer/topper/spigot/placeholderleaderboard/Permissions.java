package me.hsgamer.topper.spigot.placeholderleaderboard;

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
}
