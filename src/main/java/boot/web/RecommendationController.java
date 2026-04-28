package boot.web;

import boot.domain.BestOffer;
import boot.domain.Job;
import boot.service.BestJobFinderTask;
import boot.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final BestJobFinderTask bestJobFinderTask;

    public RecommendationController(RecommendationService recommendationService, BestJobFinderTask bestJobFinderTask) {
        this.recommendationService = recommendationService;
        this.bestJobFinderTask = bestJobFinderTask;
    }

    @GetMapping("/{userName}")
    public List<Job> suggest(@PathVariable String userName, @RequestParam(defaultValue = "2") int limit) {
        return recommendationService.suggestJobs(userName, limit);
    }

    @GetMapping("/best")
    public List<BestOffer> bestOffers() {
        return recommendationService.findBestOffers();
    }

    @PostMapping("/run")
    public List<BestOffer> runBestOfferSearch() {
        bestJobFinderTask.run();
        return recommendationService.findBestOffers();
    }
}
