/*
 * @ (#) AmenityService.java       1.0     15/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 15/04/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.mapper.AmenityMapper;
import vn.edu.iuh.fit.roomservice.model.dto.AmenityDTO;
import vn.edu.iuh.fit.roomservice.repository.AmenityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public List<AmenityDTO> getAmenities() {
        return amenityRepository.findAll()
                .stream().map(amenityMapper::toDTO)
                .collect(Collectors.toList());
    }
}
