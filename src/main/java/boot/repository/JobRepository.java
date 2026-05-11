package boot.repository;

import boot.domain.Job;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class JobRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Job> jobRowMapper = (rs, rowNum) -> new Job(
            rs.getString("title"),
            rs.getString("company"),
            findTagsByJobTitle(rs.getString("title")),
            rs.getInt("exp")
    );

    public JobRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Job> findAll() {
        return jdbcTemplate.query("""
                SELECT title, company, exp
                FROM jobs
                ORDER BY title
                """, Map.of(), jobRowMapper);
    }

    public Optional<Job> findByTitle(String title) {
        List<Job> jobs = jdbcTemplate.query("""
                SELECT title, company, exp
                FROM jobs
                WHERE title = :title
                """, Map.of("title", title), jobRowMapper);
        return jobs.stream().findFirst();
    }

    public boolean existsByTitle(String title) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM jobs
                WHERE title = :title
                """, Map.of("title", title), Long.class);
        return count != null && count > 0;
    }

    @Transactional
    public Job save(Job job) {
        MapSqlParameterSource jobParams = new MapSqlParameterSource()
                .addValue("title", job.getTitle())
                .addValue("company", job.getCompany() == null ? "" : job.getCompany())
                .addValue("exp", job.getExp());
        int updated = jdbcTemplate.update("""
                UPDATE jobs
                SET company = :company,
                    exp = :exp
                WHERE title = :title
                """, jobParams);
        if (updated == 0) {
            jdbcTemplate.update("""
                    INSERT INTO jobs (title, company, exp)
                    VALUES (:title, :company, :exp)
                    """, jobParams);
        }

        jdbcTemplate.update("""
                DELETE FROM job_tags
                WHERE job_title = :title
                """, Map.of("title", job.getTitle()));
        for (String tag : job.getTags()) {
            jdbcTemplate.update("""
                    INSERT INTO job_tags (job_title, tag)
                    VALUES (:jobTitle, :tag)
                    """, Map.of("jobTitle", job.getTitle(), "tag", tag));
        }
        return new Job(
                job.getTitle(),
                job.getCompany() == null ? "" : job.getCompany(),
                job.getTags(),
                job.getExp()
        );
    }

    public boolean deleteByTitle(String title) {
        int deleted = jdbcTemplate.update("""
                DELETE FROM jobs
                WHERE title = :title
                """, Map.of("title", title));
        return deleted > 0;
    }

    private Set<String> findTagsByJobTitle(String title) {
        return new TreeSet<>(jdbcTemplate.query("""
                SELECT tag
                FROM job_tags
                WHERE job_title = :title
                ORDER BY tag
                """, Map.of("title", title), (rs, rowNum) -> rs.getString("tag")));
    }
}
