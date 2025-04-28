package vn.edu.iuh.fit.roomservice.enumvalue;

import lombok.Getter;

@Getter
public enum RoomStatus {
    PENDING("PENDING"), APPROVED("APPROVED"), REJECTED("REJECTED"), HIDDEN("HIDDEN");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }
}
