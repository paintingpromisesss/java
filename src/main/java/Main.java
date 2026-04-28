import boot.repository.CommandHistoryRepository;
import boot.repository.JobRepository;
import boot.repository.UserRepository;
import boot.service.BestJobFinderTask;
import boot.service.CommandService;
import boot.service.JobService;
import boot.service.RecommendationService;
import boot.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final long BEST_JOB_PERIOD_SECONDS = 60;

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        JobRepository jobRepository = new JobRepository();
        CommandHistoryRepository commandHistoryRepository = CommandHistoryRepository.forFile("commands.txt");

        UserService userService = new UserService(userRepository);
        JobService jobService = new JobService(jobRepository);
        RecommendationService recommendationService = new RecommendationService(userRepository, jobRepository);
        CommandService commandService = new CommandService(
                userService,
                jobService,
                recommendationService,
                commandHistoryRepository
        );
        commandService.restoreData();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "best-job-finder");
                thread.setDaemon(true);
                return thread;
            }
        });
        executor.scheduleAtFixedRate(
                new BestJobFinderTask(recommendationService),
                0,
                BEST_JOB_PERIOD_SECONDS,
                TimeUnit.SECONDS
        );

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.equals("exit")) {
                shutdownExecutor(executor);
                return;
            }

            List<String> result = commandService.handleCommand(line, true);
            for (String outputLine : result) {
                System.out.println(outputLine);
            }
        }
        shutdownExecutor(executor);
    }

    static void shutdownExecutor(ScheduledExecutorService executor) {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                executor.awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
            Thread.currentThread().interrupt();
        }
    }
}
