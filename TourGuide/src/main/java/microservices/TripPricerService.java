package microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tour_guide.model.trip_pricer.Provider;

import java.util.List;

@FeignClient(name = "tripPricer", url = "http://tripPricer:8083/api/v1" +
        "/tripPricer/")
public interface TripPricerService {

    @GetMapping("/getPrice")
    ResponseEntity<List<Provider>> getPrice(
            @RequestParam String apikey
            , @RequestParam String attractionId
            , @RequestParam int adults
            , @RequestParam int children
            , @RequestParam int nightsStay
            , @RequestParam int rewardsPoints
    );

    @GetMapping("/getProviderName")
    public ResponseEntity<String> getProviderName(@RequestParam String apiKey
            , @RequestParam int adults);
}
