package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface DatabaseConfig {
    @ConfigPath("host")
    default String getHost() {
        return "localhost";
    }

    @ConfigPath("port")
    default String getPort() {
        return "3306";
    }

    @ConfigPath("database")
    default String getDatabase() {
        return "topper";
    }

    @ConfigPath("username")
    default String getUsername() {
        return "root";
    }

    @ConfigPath("password")
    default String getPassword() {
        return "";
    }

    @ConfigPath("use-ssl")
    default boolean isUseSSL() {
        return false;
    }
}
