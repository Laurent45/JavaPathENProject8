package tourGuide;

import microservices.GpsUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	
	@Autowired
	private GpsUtilService gpsUtilService;
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(gpsUtilService, getRewardCentral());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}
