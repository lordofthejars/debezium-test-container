package org.debezium;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testcontainers.containers.JdbcDatabaseContainer;

public class Configuration {

    private static final String CONNECTOR = "connector.class";
    private static final String HOSTNAME = "database.hostname";
    private static final String PORT = "database.port";
    private static final String USER = "database.user";
    private static final String PASSWORD = "database.password";
    private static final String DBNAME = "database.dbname";

    private Map<String, String> configuration = new HashMap<>();
    
    private Configuration() {
    }

    public static Configuration create() {
        return new Configuration();
    }

    public static Configuration fromPostgreSQL(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        Configuration configuration = new Configuration();

        configuration.with(HOSTNAME, jdbcDatabaseContainer.getContainerInfo().getConfig().getHostName());
        
        final List<Integer> exposedPorts = jdbcDatabaseContainer.getExposedPorts();
        configuration.with(PORT, exposedPorts.get(0));

        configuration.with(USER, jdbcDatabaseContainer.getUsername());
        configuration.with(PASSWORD, jdbcDatabaseContainer.getPassword());

        configuration.with(CONNECTOR,
                ConnectorResolver.getConnectorByJdbcDriver(jdbcDatabaseContainer.getDriverClassName()));
        configuration.with(DBNAME, jdbcDatabaseContainer.getDatabaseName());

        return configuration;
    }

    public Configuration with(String key, String value) {
        this.configuration.put(key, value);
        return this;
    }

    public Configuration with(String key, Integer value) {
        return this.with(key, Integer.toString(value));
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

}