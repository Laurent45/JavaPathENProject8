package tourGuide.dto;


import tourGuide.model.gpsUtil.Location;

import java.util.List;

public class UserNearestAttractionDTO {

    private final Location userLocation;
    private final List<NearByAttractionDTO> nearestAttraction;

    public UserNearestAttractionDTO(Location userLocation
            , List<NearByAttractionDTO> nearestAttraction) {
        this.userLocation = userLocation;
        this.nearestAttraction = nearestAttraction;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public List<NearByAttractionDTO> getNearestAttraction() {
        return nearestAttraction;
    }
}
