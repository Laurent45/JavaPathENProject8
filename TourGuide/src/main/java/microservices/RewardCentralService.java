package microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rewardCentral", url = "http://localhost:8082/api/v1" +
        "/rewardCentral/")
public interface RewardCentralService {

    @GetMapping("/attractionRewardPoints")
    ResponseEntity<Integer> getAttractionRewardPoints(
            @RequestParam String attractionId
            , @RequestParam String userId);
}
