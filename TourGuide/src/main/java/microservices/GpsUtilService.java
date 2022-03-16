package microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tour_guide.model.gps_util.Attraction;
import tour_guide.model.gps_util.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "gpsutil", url = "${gpsutil}")
public interface GpsUtilService {

    @GetMapping("userLocation/{userId}")
    VisitedLocation getUserLocation(@PathVariable UUID userId);

    @GetMapping("attractions")
    List<Attraction> getAttractions();
}
