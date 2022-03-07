package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import microservices.GpsUtilService;
import microservices.RewardCentralService;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import tourGuide.dto.UserNearestAttractionDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.gpsUtil.VisitedLocation;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Autowired
	private GpsUtilService gpsUtil;
	@Autowired
	private RewardCentralService rewardCentralService;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void getUserLocation() {
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
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
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2" +
				"@tourGuide.com", null);

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		
		UserNearestAttractionDTO userNearestAttraction =
				tourGuideService.getNearByAttractions(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, userNearestAttraction.getNearestAttraction().size());
	}
	
	public void getTripDeals() {
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}
