package vn.edu.iuh.fit.roomservice.enumvalue;

import lombok.Getter;

@Getter
public enum RoomType {
    BOARDING_HOUSE("BOARDING_HOUSE"),     // Nhà trọ - Phòng trọ
    WHOLE_HOUSE("WHOLE_HOUSE"),           // Nhà nguyên căn
    APARTMENT("APARTMENT");               // Căn hộ

    private final String value;

    RoomType(String value) {
        this.value = value;
    }

}

