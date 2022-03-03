package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.dto.UserNearestAttractionDTO;
import tourGuide.model.gpsUtil.Location;
import tourGuide.model.gpsUtil.VisitedLocation;
import tourGuide.model.user.User;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearbyAttractions") 
    public ResponseEntity<UserNearestAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
    	User user = getUser(userName);
        return ResponseEntity.ok(tourGuideService.getNearByAttractions(user));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() {
        return ResponseEntity.ok(tourGuideService.getAllCurrentLocations());
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}