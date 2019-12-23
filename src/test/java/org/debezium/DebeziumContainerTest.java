package org.debezium;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class DebeziumContainerTest {

    private static PostgreSQLContainer postgreContainer = new PostgreSQLContainer<>("debezium/postgres:11");
    private static KafkaContainer kafkaContainer = new KafkaContainer("5.3.1");
    private static DebeziumContainer debeziumContainer = new DebeziumContainer("1.0.0.Final");

    @BeforeAll
    static void initializeInfrastructure() throws IOException {
        kafkaContainer.start();

        postgreContainer.withNetwork(kafkaContainer.getNetwork()).start();
        debeziumContainer.withKafka(kafkaContainer).start();
    }

    @Test
    public void runTest() throws IOException {

        // Given

        final Configuration configuration = Configuration.fromPostgreSQL(postgreContainer);
        configuration.with("tasks.max", 1);
        configuration.with("database.server.name", "dbserver1");
        configuration.with("table.whitelist", "public.outbox");

        final Connector connector = Connector.from("my-connector", configuration);

        // When

        debeziumContainer.registerConnector(connector);

        // Then
 
        final String connectorInfo = readConnector(debeziumContainer.getConnectorTarget("my-connector"));
        final JSONObject response = new JSONObject(connectorInfo);
        
        final JSONArray tasks = response.getJSONArray("tasks");
        assertThat(tasks.length()).isEqualTo(1);

        final JSONObject expectedTask = tasks.getJSONObject(0);
        assertThat(expectedTask.getString("connector")).isEqualTo("my-connector");

        
    }

    private String readConnector(String url) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://" + url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}