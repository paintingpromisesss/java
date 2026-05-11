package boot.repository;

import boot.domain.User;
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
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getString("name"),
            findSkillsByUserName(rs.getString("name")),
            rs.getInt("exp")
    );

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return jdbcTemplate.query("""
                SELECT name, exp
                FROM users
                ORDER BY name
                """, Map.of(), userRowMapper);
    }

    public Optional<User> findByName(String name) {
        List<User> users = jdbcTemplate.query("""
                SELECT name, exp
                FROM users
                WHERE name = :name
                """, Map.of("name", name), userRowMapper);
        return users.stream().findFirst();
    }

    public boolean existsByName(String name) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM users
                WHERE name = :name
                """, Map.of("name", name), Long.class);
        return count != null && count > 0;
    }

    @Transactional
    public User save(User user) {
        MapSqlParameterSource userParams = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("exp", user.getExp());
        int updated = jdbcTemplate.update("""
                UPDATE users
                SET exp = :exp
                WHERE name = :name
                """, userParams);
        if (updated == 0) {
            jdbcTemplate.update("""
                    INSERT INTO users (name, exp)
                    VALUES (:name, :exp)
                    """, userParams);
        }

        jdbcTemplate.update("""
                DELETE FROM user_skills
                WHERE user_name = :name
                """, Map.of("name", user.getName()));
        for (String skill : user.getSkills()) {
            jdbcTemplate.update("""
                    INSERT INTO user_skills (user_name, skill)
                    VALUES (:userName, :skill)
                    """, Map.of("userName", user.getName(), "skill", skill));
        }
        return new User(user.getName(), user.getSkills(), user.getExp());
    }

    public boolean deleteByName(String name) {
        int deleted = jdbcTemplate.update("""
                DELETE FROM users
                WHERE name = :name
                """, Map.of("name", name));
        return deleted > 0;
    }

    private Set<String> findSkillsByUserName(String name) {
        return new TreeSet<>(jdbcTemplate.query("""
                SELECT skill
                FROM user_skills
                WHERE user_name = :name
                ORDER BY skill
                """, Map.of("name", name), (rs, rowNum) -> rs.getString("skill")));
    }
}
