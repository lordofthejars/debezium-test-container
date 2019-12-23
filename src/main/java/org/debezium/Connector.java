package org.debezium;

import org.json.JSONObject;

public class Connector {
    
    private static final String NAME = "name";
    private static final String CONFIGURATION = "config";

    private String name;
    private Configuration configuration;
    
    private Connector(String name, Configuration configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public static Connector from(String name, Configuration configuration) {
        return new Connector(name, configuration);
    }

    public String toJson() {
        final JSONObject conf = new JSONObject(this.configuration.getConfiguration());
        final JSONObject connector = new JSONObject();
        connector.put(NAME, this.name);
        connector.put(CONFIGURATION, conf);

        return connector.toString();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getName() {
        return name;
    }
}