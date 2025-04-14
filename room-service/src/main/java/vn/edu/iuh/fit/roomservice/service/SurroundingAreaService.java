/*
 * @ (#) SurroundingAreaService.java       1.0     15/04/2025
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
import vn.edu.iuh.fit.roomservice.mapper.SurroundingAreaMapper;
import vn.edu.iuh.fit.roomservice.model.dto.SurroundingAreaDTO;
import vn.edu.iuh.fit.roomservice.model.entity.SurroundingArea;
import vn.edu.iuh.fit.roomservice.repository.SurroundingAreaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurroundingAreaService {
    private final SurroundingAreaRepository surroundingAreaRepository;
    private final SurroundingAreaMapper surroundingAreaMapper;

    public List<SurroundingAreaDTO> getAllSurroundingAreas() {
        return surroundingAreaRepository.findAll()
                .stream().map(surroundingAreaMapper::toDTO)
                .collect(Collectors.toList());
    }
}
