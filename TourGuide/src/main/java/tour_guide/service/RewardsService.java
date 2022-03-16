package tour_guide.service;

import microservices.GpsUtilService;
import microservices.RewardCentralService;
import org.springframework.stereotype.Service;
import tour_guide.model.gps_util.Attraction;
import tour_guide.model.gps_util.Location;
import tour_guide.model.gps_util.VisitedLocation;
import tour_guide.model.user.User;
import tour_guide.model.user.UserReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final int NUMBER_OF_THREAD = 1000;

    // proximity in miles
    private static final int DEFAULT_PROXIMITY_BUFFER = 10;
    private static final int ATTRACTION_PROXIMITY_RANGE = 200;
    private int proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
    private final GpsUtilService gpsUtil;
    private final RewardCentralService rewardsCentral;

    public RewardsService(GpsUtilService gpsUtil,
                          RewardCentralService rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
    }

    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations =
                new ArrayList<>(user.getVisitedLocations());
        List<Attraction> attractions = gpsUtil.getAttractions();

        for (VisitedLocation visitedLocation : userLocations) {
            for (Attraction attraction : attractions) {
                if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))
                        && nearAttraction(visitedLocation, attraction)) {
                    user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                }
            }
        }
    }

    /**
     * Use ExecutorService in order to run many times calculate rewards with
     * multi-threading.
     *
     * @param users list of users
     */
    public void calculateRewardsUsers(List<User> users) {
        ExecutorService es = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
        List<Runnable> tasks = new ArrayList<>();
        users.forEach(user -> tasks.add(() -> calculateRewards(user)));

        List<Future<?>> futures = new ArrayList<>();
        tasks.forEach(task -> futures.add(es.submit(task)));
        futures.parallelStream().forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        es.shutdown();
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > ATTRACTION_PROXIMITY_RANGE ? false : true;
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    public int getRewardPoints(Attraction attraction, User user) {
        return Optional.ofNullable(rewardsCentral.getAttractionRewardPoints(
                String.valueOf(attraction.attractionId)
                , String.valueOf(user.getUserId())).getBody()).orElse(0);
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }

}
