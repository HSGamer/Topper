package me.hsgamer.topper.agent.storage.simple.setting;

import java.util.Map;

public class DatabaseSetting {
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean useSSL;
    private final Map<String, Object> driverProperties;
    private final Map<String, Object> clientProperties;

    public DatabaseSetting(String host, String port, String database, String username, String password, boolean useSSL, Map<String, Object> driverProperties, Map<String, Object> clientProperties) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.useSSL = useSSL;
        this.driverProperties = driverProperties;
        this.clientProperties = clientProperties;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public Map<String, Object> getDriverProperties() {
        return driverProperties;
    }

    public Map<String, Object> getClientProperties() {
        return clientProperties;
    }
}
