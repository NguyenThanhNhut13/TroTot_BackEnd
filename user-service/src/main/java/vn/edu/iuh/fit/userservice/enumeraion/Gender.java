/*
 * @ (#) Gender.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.enumeraion;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE"),
    OTHER("OTHER");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public static boolean isValid(Gender gender) {
        if (gender == null) {
            return false;
        }
        return Arrays.stream(Gender.values())
                .anyMatch(validGender -> validGender == gender);
    }
}
