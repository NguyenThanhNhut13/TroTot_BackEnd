package vn.edu.iuh.fit.roomservice.enumvalue;

import lombok.Getter;

@Getter
public enum RoomStatus {
    REGULAR_USER("REGULAR_USER"), LANDLORD("LANDLORD"), ADMIN("ADMIN");

    private final String value;

    private RoomStatus(String value) {
        this.value = value;
    }
}
