package tour_guide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tour_guide.helper.InternalTestHelper;
import tour_guide.model.gps_util.Attraction;
import tour_guide.model.gps_util.VisitedLocation;
import tour_guide.service.RewardsService;
import tour_guide.service.TourGuideService;
import tour_guide.model.user.User;
import tour_guide.model.user.UserReward;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRewardsService {

	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardsService;

	@BeforeClass
	public static void beforeClass() {
		InternalTestHelper.setInternalUserNumber(0);
	}

	@Test
	public void userGetRewards() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide" +
				".com", null);
		Attraction attraction = new Attraction("Disneyland"
				, "Anaheim"
				, "CA"
				, 33.817595D
				, -117.922008D);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
				attraction,
				new Date()));
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = new Attraction("Disneyland"
				, "Anaheim"
				, "CA"
				, 33.817595D
				, -117.922008D);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction,
				attraction));
	}
}
