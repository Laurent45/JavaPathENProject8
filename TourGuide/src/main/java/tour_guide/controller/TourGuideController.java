package tour_guide.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tour_guide.dto.UserNearestAttractionDTO;
import tour_guide.model.gps_util.Location;
import tour_guide.model.gps_util.VisitedLocation;
import tour_guide.model.trip_pricer.Provider;
import tour_guide.model.user.User;
import tour_guide.model.user.UserReward;
import tour_guide.service.TourGuideService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TourGuideController {

	private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public Location getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return visitedLocation.location;
    }

    @RequestMapping("/getNearbyAttractions") 
    public ResponseEntity<UserNearestAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
    	User user = getUser(userName);
        return ResponseEntity.ok(tourGuideService.getNearByAttractions(user));
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() {
        return ResponseEntity.ok(tourGuideService.getAllCurrentLocations());
    }
    
    @RequestMapping("/getTripDeals")
    public ResponseEntity<List<Provider>> getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        if (providers == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}