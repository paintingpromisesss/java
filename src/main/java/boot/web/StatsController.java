package boot.web;

import boot.domain.Job;
import boot.domain.User;
import boot.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final RecommendationService recommendationService;

    public StatsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/jobs")
    public List<Job> jobsWithMinExp(@RequestParam(defaultValue = "0") int minExp) {
        return recommendationService.findJobsWithMinExp(minExp);
    }

    @GetMapping("/users")
    public List<User> usersWithMinMatches(@RequestParam(defaultValue = "0") int minMatches) {
        return recommendationService.findUsersWithMinMatches(minMatches);
    }

    @GetMapping("/top-skills")
    public List<String> topSkills(@RequestParam(defaultValue = "5") int limit) {
        return recommendationService.findTopSkills(limit);
    }
}
