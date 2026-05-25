package boot.service;

import boot.domain.Job;
import boot.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestService {
    private final UserService userService;
    private final JobService jobService;

    public SuggestService(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    public List<Job> suggest(String userName) {
        Optional<User> user = userService.findByName(userName);
        if (user.isEmpty()) {
            return List.of();
        }

        return jobService.findAll().stream()
                .filter(job -> getScore(user.get(), job) > 0)
                .sorted((a, b) -> {
                    int scoreCompare = Double.compare(getScore(user.get(), b), getScore(user.get(), a));
                    if (scoreCompare != 0) {
                        return scoreCompare;
                    }
                    return a.getTitle().compareTo(b.getTitle());
                })
                .toList();
    }

    private double getScore(User user, Job job) {
        int matches = 0;
        for (String skill : user.getSkills()) {
            if (job.getTags().contains(skill)) {
                matches++;
            }
        }

        if (user.getExp() < job.getExp()) {
            return matches / 2.0;
        }

        return matches;
    }
}
