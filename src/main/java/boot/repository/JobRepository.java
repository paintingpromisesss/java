package boot.repository;

import boot.domain.Job;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JobRepository {
    private final Map<String, Job> jobs = new LinkedHashMap<>();

    public synchronized List<Job> findAll() {
        List<Job> result = new ArrayList<>();
        for (Job job : jobs.values()) {
            result.add(copy(job));
        }
        return result;
    }

    public synchronized Optional<Job> findByTitle(String title) {
        Job job = jobs.get(title);
        if (job == null) {
            return Optional.empty();
        }
        return Optional.of(copy(job));
    }

    public synchronized boolean existsByTitle(String title) {
        return jobs.containsKey(title);
    }

    public synchronized Job save(Job job) {
        Job copy = copy(job);
        jobs.put(copy.getTitle(), copy);
        return copy(copy);
    }

    public synchronized boolean deleteByTitle(String title) {
        return jobs.remove(title) != null;
    }

    private Job copy(Job job) {
        return new Job(job.getTitle(), job.getCompany(), job.getTags(), job.getExp());
    }
}
