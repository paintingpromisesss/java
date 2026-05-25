package boot.service;

import boot.domain.Job;
import boot.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private SuggestService suggestService;

    @Test
    void suggestTest() {
        User user = new User("alice", Set.of("java", "spring"), 3);
        Job javaDeveloper = new Job("Java_Developer", "VK", Set.of("java", "spring"), 2);
        Job backendDeveloper = new Job("Backend_Developer", "Yandex", Set.of("java", "sql"), 4);
        Job designer = new Job("Designer", "Studio", Set.of("figma"), 1);

        when(userService.findByName("alice")).thenReturn(Optional.of(user));
        when(jobService.findAll()).thenReturn(List.of(designer, backendDeveloper, javaDeveloper));

        List<Job> result = suggestService.suggest("alice");

        assertEquals(List.of(javaDeveloper, backendDeveloper), result);
    }

    @Test
    void emptyVacanciesTest() {
        User user = new User("alice", Set.of("java"), 1);

        when(userService.findByName("alice")).thenReturn(Optional.of(user));
        when(jobService.findAll()).thenReturn(List.of());

        List<Job> result = suggestService.suggest("alice");

        assertTrue(result.isEmpty());
    }

    @Test
    void singleVacancyTest() {
        User user = new User("alice", Set.of("java"), 2);
        Job javaDeveloper = new Job("Java_Developer", "VK", Set.of("java"), 1);

        when(userService.findByName("alice")).thenReturn(Optional.of(user));
        when(jobService.findAll()).thenReturn(List.of(javaDeveloper));

        List<Job> result = suggestService.suggest("alice");

        assertEquals(1, result.size());
        assertEquals(javaDeveloper, result.get(0));
    }

    @Test
    void userNotFoundTest() {
        when(userService.findByName("unknown")).thenReturn(Optional.empty());

        List<Job> result = suggestService.suggest("unknown");

        assertTrue(result.isEmpty());
        verify(jobService, never()).findAll();
    }
}
