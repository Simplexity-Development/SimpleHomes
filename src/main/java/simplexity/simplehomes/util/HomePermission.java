package simplexity.simplehomes.util;

import org.jetbrains.annotations.NotNull;

public enum Permission {
    HOMES_BASE("homes",)

    private final org.bukkit.permissions.Permission permission;

    Permission(org.bukkit.permissions.Permission permission) {
        this.permission = permission;
    }

    @NotNull
    public org.bukkit.permissions.Permission getPermission() {
        return permission;
    }

}
