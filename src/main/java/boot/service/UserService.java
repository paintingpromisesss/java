package boot.service;

import boot.domain.User;
import boot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    public User save(String name, Set<String> skills, int exp) {
        return userRepository.save(new User(name, skills, exp));
    }

    public Optional<User> createIfAbsent(String name, Set<String> skills, int exp) {
        if (name == null || name.isBlank() || userRepository.existsByName(name)) {
            return Optional.empty();
        }
        return Optional.of(save(name, skills, exp));
    }

    public boolean deleteByName(String name) {
        return userRepository.deleteByName(name);
    }
}
