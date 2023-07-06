package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface DatabaseConfig {
    @ConfigPath("host")
    @Comment("The host of the database")
    default String getHost() {
        return "localhost";
    }

    @ConfigPath("port")
    @Comment("The port of the database")
    default String getPort() {
        return "3306";
    }

    @ConfigPath("database")
    @Comment("The database name")
    default String getDatabase() {
        return "topper";
    }

    @ConfigPath("username")
    @Comment("The username to connect to the database")
    default String getUsername() {
        return "root";
    }

    @ConfigPath("password")
    @Comment("The password to connect to the database")
    default String getPassword() {
        return "";
    }

    @ConfigPath("use-ssl")
    @Comment("Whether to use SSL or not")
    default boolean isUseSSL() {
        return false;
    }
}
