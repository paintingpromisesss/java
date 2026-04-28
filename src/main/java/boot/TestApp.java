package boot;

import boot.service.CommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestApp {
    public static void main(String[] args) {
        if (System.getProperty("debug") == null) {
            System.setProperty("debug", "false");
        }
        SpringApplication.run(TestApp.class, args);
    }

    @Bean
    CommandLineRunner restoreData(CommandService commandService) {
        return args -> commandService.restoreData();
    }
}
