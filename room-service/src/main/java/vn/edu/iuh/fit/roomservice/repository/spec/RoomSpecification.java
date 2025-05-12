/*
 * @ (#) RoomSpecification.java       1.0     18/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.repository.spec;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 18/04/2025
 * @version:    1.0
 */

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.model.entity.*;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {

    public static Specification<Room> buildSpecification(
            List<Long> addressIds,
            Double minPrice, Double maxPrice,
            String areaRange, String roomType,
            List<String> amenityNames, List<String> environmentNames,
            List<String> targetAudienceNames, Boolean hasVideoReview
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (addressIds != null && !addressIds.isEmpty()) {
                predicates.add(root.get("addressId").in(addressIds));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (areaRange != null) {
                predicates.add(buildAreaPredicate(cb, root.get("area"), areaRange));
            }

            if (roomType != null) {
                predicates.add(cb.equal(root.get("roomType"), RoomType.valueOf(roomType)));
            }

            if (hasVideoReview != null) {
                if (hasVideoReview) {
                    predicates.add(cb.isNotNull(root.get("videoUrl")));
                } else {
                    predicates.add(cb.isNull(root.get("videoUrl")));
                }
            }

            if (amenityNames != null && !amenityNames.isEmpty()) {
                Join<Room, Amenity> amenityJoin = root.join("amenities", JoinType.INNER);
                predicates.add(amenityJoin.get("name").in(amenityNames));
            }

            if (environmentNames != null && !environmentNames.isEmpty()) {
                Join<Room, SurroundingArea> envJoin = root.join("surroundingAreas", JoinType.INNER);
                predicates.add(envJoin.get("name").in(environmentNames));
            }

            if (targetAudienceNames != null && !targetAudienceNames.isEmpty()) {
                Join<Room, TargetAudience> targetJoin = root.join("targetAudiences", JoinType.INNER);
                predicates.add(targetJoin.get("name").in(targetAudienceNames));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildAreaPredicate(CriteriaBuilder cb, Path<Double> areaPath, String areaRange) {
        return switch (areaRange) {
            case "UNDER_20" -> cb.lessThan(areaPath, 20.0);
            case "20_40" -> cb.between(areaPath, 20.0, 40.0);
            case "40_60" -> cb.between(areaPath, 40.0, 60.0);
            case "60_80" -> cb.between(areaPath, 60.0, 80.0);
            case "OVER_80" -> cb.greaterThan(areaPath, 80.0);
            default -> cb.conjunction(); // không lọc gì nếu không khớp
        };
    }
}

