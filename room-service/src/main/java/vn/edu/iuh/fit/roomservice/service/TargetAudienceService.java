/*
 * @ (#) TargetAudienceService.java       1.0     15/04/2025
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
import vn.edu.iuh.fit.roomservice.mapper.TargetAudienceMapper;
import vn.edu.iuh.fit.roomservice.model.dto.TargetAudienceDTO;
import vn.edu.iuh.fit.roomservice.repository.TargetAudienceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetAudienceService {

    private final TargetAudienceRepository targetAudienceRepository;
    private final TargetAudienceMapper targetAudienceMapper;

    public List<TargetAudienceDTO> getAllTargetAudiences() {
        return targetAudienceRepository.findAll()
                .stream().map(targetAudienceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
