package tour_guide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tour_guide.dto.UserNearestAttractionDTO;
import tour_guide.helper.InternalTestHelper;
import tour_guide.model.gps_util.VisitedLocation;
import tour_guide.model.trip_pricer.Provider;
import tour_guide.service.TourGuideService;
import tour_guide.model.user.User;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class TestTourGuideService {

	@Autowired
	private TourGuideService tourGuideService;

	@BeforeClass
	public static void avoidCreateUser() {
		InternalTestHelper.setInternalUserNumber(0);
	}

	@Test
	public void getUserLocation() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(visitedLocation.userId, user.getUserId());
	}
	
	@Test
	public void addUser() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2" +
				"@tourGuide.com", null);

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2" +
				"@tourGuide.com", null);

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertEquals(2, allUsers.size());
	}

	@Test
	public void getNearbyAttractions() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		
		UserNearestAttractionDTO userNearestAttraction =
				tourGuideService.getNearByAttractions(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, userNearestAttraction.getNearestAttraction().size());
	}

	public void getTripDeals() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}