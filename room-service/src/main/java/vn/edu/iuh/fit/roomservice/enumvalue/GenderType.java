package vn.edu.iuh.fit.roomservice.enumvalue;

import lombok.Getter;

@Getter
public enum GenderType {
    MALE("MALE"),
    FEMALE("FEMALE"),
    ALL("ALL");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }
}
