package tourGuide.dto;

import gpsUtil.location.Location;

public class NearByAttractionDTO {

    private final String attractionName;
    private final Location attractionLocation;
    private final Double distance;
    private final int rewardPoints;

    public NearByAttractionDTO(String attractionName
            , Location attractionLocation
            , Double distance
            , int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public Double getDistance() {
        return distance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }
}
