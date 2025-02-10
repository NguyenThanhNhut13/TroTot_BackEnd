package vn.edu.iuh.fit.userservice.enumvalue;

import lombok.Getter;

@Getter
public enum UserRole {
    REGULAR_USER("REGULAR_USER"), LANDLORD("LANDLORD"), ADMIN("ADMIN");

    private final String value;

    private UserRole(String value) {
        this.value = value;
    }
}
