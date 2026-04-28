package boot.service;

import boot.domain.Job;
import boot.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> findAll() {
        return jobRepository.findAll().stream()
                .sorted(Comparator.comparing(Job::getTitle))
                .toList();
    }

    public Optional<Job> findByTitle(String title) {
        return jobRepository.findByTitle(title);
    }

    public boolean existsByTitle(String title) {
        return jobRepository.existsByTitle(title);
    }

    public Job save(String title, String company, Set<String> tags, int exp) {
        return jobRepository.save(new Job(title, company, tags, exp));
    }

    public Optional<Job> createIfAbsent(String title, String company, Set<String> tags, int exp) {
        if (title == null || title.isBlank() || jobRepository.existsByTitle(title)) {
            return Optional.empty();
        }
        return Optional.of(save(title, company, tags, exp));
    }

    public boolean deleteByTitle(String title) {
        return jobRepository.deleteByTitle(title);
    }
}
