package me.hsgamer.topper.placeholderleaderboard;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.permission.PermissionComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions extends PermissionComponent {
    public static final Permission RELOAD = new Permission("topper.reload", PermissionDefault.OP);

    public Permissions(BasePlugin plugin) {
        super(plugin);
    }
}
