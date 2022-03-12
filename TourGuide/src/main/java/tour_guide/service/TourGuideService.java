package tour_guide.service;

import microservices.GpsUtilService;
import microservices.TripPricerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tour_guide.dto.NearByAttractionDTO;
import tour_guide.dto.UserNearestAttractionDTO;
import tour_guide.helper.InternalTestHelper;
import tour_guide.model.gps_util.Location;
import tour_guide.model.gps_util.VisitedLocation;
import tour_guide.model.trip_pricer.Provider;
import tour_guide.model.user.User;
import tour_guide.model.user.UserPreferences;
import tour_guide.model.user.UserReward;
import tour_guide.tracker.Tracker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    private static final int NUMBER_OF_THREAD = 1000;

    private final GpsUtilService gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricerService tripPricer;
    public final Tracker tracker;
    boolean testMode = true;

    public TourGuideService(GpsUtilService gpsUtil
            , RewardsService rewardsService
            , TripPricerService tripPricer) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        this.tripPricer = tripPricer;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        return (user.getVisitedLocations().size() > 0) ?
                        user.getLastVisitedLocation() :
                        trackUserLocation(user);
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(TRIP_PRICER_API_KEY, String.valueOf(user.getUserId()), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(), cumulativeRewardPoints).getBody();
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        return visitedLocation;
    }

    /**
     * Track all users' location. Use an executor service with a pool thread
     * which invoke trackUserLocation.
     *
     * @param users List of users
     * @return A list of visitedLocation
     */
    public List<VisitedLocation> trackAllUserLocation(List<User> users) {
        ExecutorService es = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
        List<Callable<VisitedLocation>> tasks = new ArrayList<>();
        // Create a task for each user.
        users.forEach(user -> tasks.add(() -> trackUserLocation(user)));

        // Invoke each task.
        List<Future<VisitedLocation>> listFuture = null;
        try {
            listFuture = es.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        // Get each result of tasks and return it
        List<VisitedLocation> locations =
                Objects.requireNonNull(listFuture).stream()
                        .map(visitedLocationFuture -> {
                            try {
                                return visitedLocationFuture.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }
                            return null;
                        })
                        .collect(Collectors.toList());

        es.shutdown();
        return locations;
    }

    /**
     * Get the user location and the five the nearest attractions of the
     * position of the user.
     *
     * @param user
     * @return Return each information in UserNearestAttractionDTO
     */
    public UserNearestAttractionDTO getNearByAttractions(User user) {
        VisitedLocation visitedLocation = getUserLocation(user);

        List<NearByAttractionDTO> nearestAttraction =
                gpsUtil.getAttractions().stream()
                        // Create a map, key = attraction / value = distance
                        // between attraction location and user location
                        .collect(Collectors.toMap(a -> a,
                                a -> rewardsService.getDistance(new Location(a.latitude, a.longitude), visitedLocation.location)))
                        .entrySet().stream()
                        // Sort by distance
                        .sorted(Map.Entry.comparingByValue())
                        .limit(5)
                        // Map these five result to NearByAttractionDTO
                        .map(entry ->
                                new NearByAttractionDTO(entry.getKey().attractionName
                                        , new Location(entry.getKey().latitude, entry.getKey().longitude)
                                        , entry.getValue()
                                        , rewardsService.getRewardPoints(entry.getKey()
                                        , user)))
                        .collect(Collectors.toList());
        return new UserNearestAttractionDTO(visitedLocation.location, nearestAttraction);
    }

    /**
     * A stream of all user is created in order to map to a map with the key
     * is userId and the value is the location's user.
     *
     * @return a map<userId / location's user>
     */
    public Map<UUID, Location> getAllCurrentLocations() {
        return getAllUsers().stream()
                .collect(Collectors.toMap(User::getUserId,
                        user -> (user.getVisitedLocations().isEmpty()) ?
                                new Location(0, 0) :
                                user.getLastVisitedLocation().location));
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email,
                    new UserPreferences(1, 1, 1, 1));
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> user
                .addToVisitedLocations(new VisitedLocation(user.getUserId()
                        , new Location(generateRandomLatitude(), generateRandomLongitude())
                        , getRandomTime())));
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
