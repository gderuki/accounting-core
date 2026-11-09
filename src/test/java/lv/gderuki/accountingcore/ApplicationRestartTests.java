package lv.gderuki.accountingcore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ApplicationRestartTests {

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17.6-alpine")
            .withDatabaseName("accounting")
            .withUsername("accounting")
            .withPassword("accounting");

    @Test
    void startsTwiceAgainstTheSameDatabase() {
        for (int attempt = 0; attempt < 2; attempt++) {
            try (var context = new SpringApplicationBuilder(AccountingCoreApplication.class).run(
                    "--server.port=0",
                    "--spring.datasource.url=" + postgres.getJdbcUrl(),
                    "--spring.datasource.username=" + postgres.getUsername(),
                    "--spring.datasource.password=" + postgres.getPassword())) {
                var jdbc = context.getBean(JdbcTemplate.class);
                assertThat(jdbc.queryForObject(
                        "select count(*) from public.flyway_schema_history where version = '1' and success",
                        Integer.class)).isEqualTo(1);
            }
        }
    }
}
