package boot.service;

import boot.TestApp;
import boot.domain.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = TestApp.class,
        properties = "app.best-job-initial-delay-ms=600000"
)
@Testcontainers
class SuggestServiceIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("job_app_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private SuggestService suggestService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE user_skills, job_tags, users, jobs CASCADE");
    }

    @Test
    void suggestTest() {
        userService.save("alice", Set.of("java", "spring"), 3);
        userService.save("bob", Set.of("python"), 2);
        Job javaDeveloper = jobService.save("Java_Developer", "VK", Set.of("java", "spring"), 2);
        Job backendDeveloper = jobService.save("Backend_Developer", "Yandex", Set.of("java", "sql"), 4);
        jobService.save("Python_Developer", "Ozon", Set.of("python"), 1);

        List<Job> result = suggestService.suggest("alice");

        assertEquals(2, result.size());
        assertEquals(javaDeveloper.getTitle(), result.get(0).getTitle());
        assertEquals(backendDeveloper.getTitle(), result.get(1).getTitle());
    }
}
