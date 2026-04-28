package boot.web;

import boot.domain.Job;
import boot.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<Job> findAll() {
        return jobService.findAll();
    }

    @GetMapping("/{title}")
    public ResponseEntity<Job> findByTitle(@PathVariable String title) {
        return jobService.findByTitle(title)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Job> create(@RequestBody JobRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (jobService.existsByTitle(request.title())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Job job = jobService.save(request.title(), request.company(), request.tags(), request.exp());
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> delete(@PathVariable String title) {
        if (!jobService.deleteByTitle(title)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    public record JobRequest(String title, String company, Set<String> tags, int exp) {
    }
}
