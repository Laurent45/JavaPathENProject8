package unit_test;


import microservices.GpsUtilService;
import microservices.RewardCentralService;
import microservices.TripPricerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tour_guide.helper.InternalTestHelper;
import tour_guide.model.user.User;
import tour_guide.model.user.UserPreferences;
import tour_guide.service.TourGuideService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
public class TourGuideServiceUT {

    @Mock
    private GpsUtilService gpsUtilServiceMock;
    @Mock
    private RewardCentralService rewardCentralServiceMock;
    @Mock
    private TripPricerService tripPricerServiceMock;

    @InjectMocks
    private TourGuideService tourGuideServiceUT;

    @Before
    public void setUp() throws Exception {
        InternalTestHelper.setInternalUserNumber(0);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserNameAndPreferences_whenUpdatePreferences_thenReturnUserUpdated() {
        UserPreferences userPreferences = new UserPreferences(
                2,
                3,
                2,
                4
        );
        UserPreferences preferencesUpdate = new UserPreferences(
                9,
                10,
                5,
                12
        );
        User user = new User(
                UUID.randomUUID(),
                "userName",
                "000-111-222",
                "emailAddress",
                userPreferences
        );
        tourGuideServiceUT.addUser(user);
        tourGuideServiceUT.updateUserPreferences("userName", preferencesUpdate);

        UserPreferences result = tourGuideServiceUT.getUser("userName").getUserPreferences();
        assertThat(result.getTicketQuantity()).isEqualTo(preferencesUpdate.getTicketQuantity());
        assertThat(result.getTripDuration()).isEqualTo(preferencesUpdate.getTripDuration());
        assertThat(result.getNumberOfAdults()).isEqualTo(preferencesUpdate.getNumberOfAdults());
        assertThat(result.getNumberOfChildren()).isEqualTo(preferencesUpdate.getNumberOfChildren());

    }
}
