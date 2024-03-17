package me.hsgamer.topper.placeholderleaderboard;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.permission.PermissionComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions extends PermissionComponent {
    public static final Permission SIGN_BREAK = new Permission("topper.sign.break", PermissionDefault.OP);
    public static final Permission SIGN = new Permission("topper.sign", PermissionDefault.OP);
    public static final Permission SKULL_BREAK = new Permission("topper.skull.break", PermissionDefault.OP);
    public static final Permission SKULL = new Permission("topper.skull", PermissionDefault.OP);
    public static final Permission TOP = new Permission("topper.top", PermissionDefault.OP);
    public static final Permission RELOAD = new Permission("topper.reload", PermissionDefault.OP);

    public Permissions(BasePlugin plugin) {
        super(plugin);
    }
}
