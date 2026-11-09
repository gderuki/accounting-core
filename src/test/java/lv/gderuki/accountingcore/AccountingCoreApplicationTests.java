package lv.gderuki.accountingcore;

import org.junit.jupiter.api.Test;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AccountingCoreApplicationTests {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17.6-alpine");

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private Flyway flyway;

    @LocalServerPort
    private int port;

    @Test
    void databaseAndMigrationsAreReady() {
        assertThat(jdbc.queryForObject("select current_database()", String.class))
                .isEqualTo(postgres.getDatabaseName());
        assertThat(jdbc.queryForObject(
                "select count(*) from information_schema.schemata where schema_name = 'accounting'",
                Integer.class)).isEqualTo(1);
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        assertThat(flyway.info().pending()).isEmpty();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
    }

    @Test
    void healthEndpointReportsUp() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(
                    URI.create("http://localhost:" + port + "/actuator/health")).GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(JsonMapper.builder().build().readTree(response.body()).path("status").asString())
                    .isEqualTo("UP");
        }
    }

}
