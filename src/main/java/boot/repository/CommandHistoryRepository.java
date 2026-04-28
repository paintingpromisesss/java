package boot.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class CommandHistoryRepository {
    private final Path path;

    @Autowired
    public CommandHistoryRepository(@Value("${app.command-history-file:commands.txt}") String fileName) {
        this(Paths.get(fileName));
    }

    private CommandHistoryRepository(Path path) {
        this.path = path;
    }

    public static CommandHistoryRepository forFile(String fileName) {
        return new CommandHistoryRepository(Paths.get(fileName));
    }

    public void save(String command) {
        try {
            Files.write(
                    path,
                    Collections.singletonList(command),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {
        }
    }

    public List<String> readAll() {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
