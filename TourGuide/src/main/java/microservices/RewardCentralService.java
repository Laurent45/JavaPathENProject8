package microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rewardCentral", url = "${rewardcentral}")
public interface RewardCentralService {

    @GetMapping("/attractionRewardPoints")
    ResponseEntity<Integer> getAttractionRewardPoints(
            @RequestParam String attractionId
            , @RequestParam String userId);
}
