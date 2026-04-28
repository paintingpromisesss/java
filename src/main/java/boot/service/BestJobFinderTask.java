package boot.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BestJobFinderTask implements Runnable {
    private final RecommendationService recommendationService;

    public BestJobFinderTask(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public void run() {
        recommendationService.printBestOffers();
    }

    @Scheduled(
            fixedRateString = "${app.best-job-period-ms:60000}",
            initialDelayString = "${app.best-job-initial-delay-ms:1000}"
    )
    public void runScheduled() {
        run();
    }
}
