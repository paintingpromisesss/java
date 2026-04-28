package boot.service;

import boot.domain.Job;
import boot.domain.User;
import boot.repository.CommandHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class CommandService {
    private final UserService userService;
    private final JobService jobService;
    private final RecommendationService recommendationService;
    private final CommandHistoryRepository commandHistoryRepository;

    public CommandService(
            UserService userService,
            JobService jobService,
            RecommendationService recommendationService,
            CommandHistoryRepository commandHistoryRepository
    ) {
        this.userService = userService;
        this.jobService = jobService;
        this.recommendationService = recommendationService;
        this.commandHistoryRepository = commandHistoryRepository;
    }

    public void restoreData() {
        for (String command : commandHistoryRepository.readAll()) {
            if (command.startsWith("user ") || command.startsWith("job ")) {
                handleCommand(command, false);
            }
        }
    }

    public List<String> handleCommand(String line, boolean needSave) {
        if (line.equals("user-list")) {
            return saveAndReturn(line, needSave, userService.findAll().stream()
                    .map(User::toString)
                    .toList());
        }

        if (line.equals("job-list")) {
            return saveAndReturn(line, needSave, jobService.findAll().stream()
                    .map(Job::toString)
                    .toList());
        }

        if (line.equals("history")) {
            return saveAndReturn(line, needSave, commandHistoryRepository.readAll());
        }

        if (line.startsWith("user ")) {
            addUser(line);
            return saveAndReturn(line, needSave, List.of());
        }

        if (line.startsWith("job ")) {
            addJob(line);
            return saveAndReturn(line, needSave, List.of());
        }

        if (line.startsWith("suggest ")) {
            String userName = line.substring(8).trim();
            return saveAndReturn(line, needSave, recommendationService.suggestJobs(userName, 2).stream()
                    .map(Job::toString)
                    .toList());
        }

        if (line.startsWith("stat ")) {
            return saveAndReturn(line, needSave, handleStat(line));
        }

        return List.of();
    }

    private List<String> saveAndReturn(String line, boolean needSave, List<String> result) {
        if (needSave) {
            commandHistoryRepository.save(line);
        }
        return result;
    }

    private void addUser(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 2) {
            return;
        }

        String name = parts[1];
        Map<String, String> params = parseParams(parts, 2);
        Set<String> skills = parseSet(params.get("--skills"));
        int exp = parseInt(params.get("--exp"));

        userService.createIfAbsent(name, skills, exp);
    }

    private void addJob(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 2) {
            return;
        }

        String title = parts[1];
        Map<String, String> params = parseParams(parts, 2);
        String company = params.getOrDefault("--company", "");
        Set<String> tags = parseSet(params.get("--tags"));
        int exp = parseInt(params.get("--exp"));

        jobService.createIfAbsent(title, company, tags, exp);
    }

    private List<String> handleStat(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 3) {
            return List.of();
        }

        String type = parts[1];
        int n = parseInt(parts[2]);

        if (type.equals("--exp")) {
            return recommendationService.findJobsWithMinExp(n).stream()
                    .map(Job::toString)
                    .toList();
        }

        if (type.equals("--match")) {
            return recommendationService.findUsersWithMinMatches(n).stream()
                    .map(User::toString)
                    .toList();
        }

        if (type.equals("--top-skills")) {
            return recommendationService.findTopSkills(n);
        }

        return List.of();
    }

    private Map<String, String> parseParams(String[] parts, int start) {
        Map<String, String> result = new HashMap<>();

        for (int i = start; i < parts.length; i++) {
            int pos = parts[i].indexOf('=');
            if (pos == -1) {
                continue;
            }

            String key = parts[i].substring(0, pos);
            String value = parts[i].substring(pos + 1);
            result.put(key, value);
        }

        return result;
    }

    private Set<String> parseSet(String value) {
        Set<String> result = new TreeSet<>();

        if (value == null || value.isEmpty()) {
            return result;
        }

        String[] parts = value.split(",");
        for (String part : parts) {
            String skill = part.trim();
            if (!skill.isEmpty()) {
                result.add(skill);
            }
        }

        return result;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
