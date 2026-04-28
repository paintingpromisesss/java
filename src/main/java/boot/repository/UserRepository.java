package boot.repository;

import boot.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<String, User> users = new LinkedHashMap<>();

    public synchronized List<User> findAll() {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            result.add(copy(user));
        }
        return result;
    }

    public synchronized Optional<User> findByName(String name) {
        User user = users.get(name);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(copy(user));
    }

    public synchronized boolean existsByName(String name) {
        return users.containsKey(name);
    }

    public synchronized User save(User user) {
        User copy = copy(user);
        users.put(copy.getName(), copy);
        return copy(copy);
    }

    public synchronized boolean deleteByName(String name) {
        return users.remove(name) != null;
    }

    private User copy(User user) {
        return new User(user.getName(), user.getSkills(), user.getExp());
    }
}
