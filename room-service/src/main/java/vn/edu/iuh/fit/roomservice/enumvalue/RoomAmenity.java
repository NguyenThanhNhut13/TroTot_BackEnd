package vn.edu.iuh.fit.roomservice.enumvalue;

import lombok.Getter;

@Getter
public enum RoomAmenity {
    WIFI("WIFI"), PARKING("PARKING"), AIR_CONDITIONER("AIR_CONDITIONER"), PRIVATE_WC("PRIVATE_WC"), KITCHEN("KITCHEN");

    private final String value;

    RoomAmenity(String value) {
        this.value = value;
    }
}
