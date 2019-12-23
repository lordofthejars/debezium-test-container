package org.debezium;

import java.util.HashMap;
import java.util.Map;

public class ConnectorResolver {

    private static final Map<String, String> driverConnector = new HashMap<>();

    static {
        driverConnector.put("org.postgresql.Driver", "io.debezium.connector.postgresql.PostgresConnector");
        driverConnector.put("com.mysql.cj.jdbc.Driver", "io.debezium.connector.mysql.MySqlConnector");
        driverConnector.put("com.mysql.jdbc.Driver", "io.debezium.connector.mysql.MySqlConnector");
        driverConnector.put("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "io.debezium.connector.sqlserver.SqlServerConnector");
    }
    
    public static String getConnectorByJdbcDriver(String jdbcDriver) {
        if (driverConnector.containsKey(jdbcDriver)) {
            return driverConnector.get(jdbcDriver);
        }

        throw new IllegalArgumentException(String.format("%s JDBC driver is passed but only %s are supported.", jdbcDriver, driverConnector.keySet().toString()));
    }

}