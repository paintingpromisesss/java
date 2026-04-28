package boot.service;

import boot.domain.BestOffer;
import boot.domain.Job;
import boot.domain.User;
import boot.repository.JobRepository;
import boot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public RecommendationService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public double getScore(User user, Job job) {
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

    public List<Job> suggestJobs(String userName, int limit) {
        Optional<User> user = userRepository.findByName(userName);
        if (user.isEmpty()) {
            return List.of();
        }

        return jobRepository.findAll().stream()
                .filter(job -> getScore(user.get(), job) > 0)
                .sorted((a, b) -> {
                    int scoreCompare = Double.compare(getScore(user.get(), b), getScore(user.get(), a));
                    if (scoreCompare != 0) {
                        return scoreCompare;
                    }
                    return a.getTitle().compareTo(b.getTitle());
                })
                .limit(limit)
                .toList();
    }

    public List<BestOffer> findBestOffers() {
        return userRepository.findAll().stream()
                .map(this::findBestOffer)
                .flatMap(Optional::stream)
                .toList();
    }

    public Optional<BestOffer> findBestOffer(User user) {
        return jobRepository.findAll().stream()
                .filter(job -> getScore(user, job) > 0)
                .sorted((a, b) -> {
                    int scoreCompare = Double.compare(getScore(user, b), getScore(user, a));
                    if (scoreCompare != 0) {
                        return scoreCompare;
                    }
                    return a.getTitle().compareTo(b.getTitle());
                })
                .findFirst()
                .map(job -> new BestOffer(user.getName(), job, getScore(user, job)));
    }

    public void printBestOffers() {
        for (BestOffer offer : findBestOffers()) {
            Job job = offer.job();
            System.out.println(offer.userName() + ", лучшее предложение — " + job.getTitle() + " at " + job.getCompany());
        }
    }

    public List<Job> findJobsWithMinExp(int exp) {
        return jobRepository.findAll().stream()
                .filter(job -> job.getExp() >= exp)
                .sorted(Comparator.comparing(Job::getTitle))
                .toList();
    }

    public List<User> findUsersWithMinMatches(int count) {
        return userRepository.findAll().stream()
                .filter(user -> countMatches(user) >= count)
                .sorted(Comparator.comparing(User::getName))
                .toList();
    }

    public List<String> findTopSkills(int limit) {
        return userRepository.findAll().stream()
                .flatMap(user -> user.getSkills().stream())
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> compareSkillCount(a, b))
                .limit(limit)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    public long countMatches(User user) {
        return jobRepository.findAll().stream()
                .filter(job -> getScore(user, job) > 0)
                .count();
    }

    private int compareSkillCount(Map.Entry<String, Long> a, Map.Entry<String, Long> b) {
        int countCompare = Long.compare(b.getValue(), a.getValue());
        if (countCompare != 0) {
            return countCompare;
        }
        return a.getKey().compareTo(b.getKey());
    }
}
