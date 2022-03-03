package microservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tourGuide.model.gpsUtil.Attraction;
import tourGuide.model.gpsUtil.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "gpsUtil", url = "http://localhost:8081/api/v1/gpsUtil/")
public interface GpsUtilService {

    @GetMapping("userLocation/{userId}")
    VisitedLocation getUserLocation(@PathVariable UUID userId);

    @GetMapping("attractions")
    List<Attraction> getAttractions();
}
