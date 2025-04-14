/*
 * @ (#) UserService.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.mapper.*;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.entity.Amenity;
import vn.edu.iuh.fit.roomservice.model.entity.Room;
import vn.edu.iuh.fit.roomservice.model.entity.SurroundingArea;
import vn.edu.iuh.fit.roomservice.model.entity.TargetAudience;
import vn.edu.iuh.fit.roomservice.repository.AmenityRepository;
import vn.edu.iuh.fit.roomservice.repository.RoomRepository;
import vn.edu.iuh.fit.roomservice.repository.SurroundingAreaRepository;
import vn.edu.iuh.fit.roomservice.repository.TargetAudienceRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AddressClient addressClient;
    private final RoomMapper roomMapper;
    private final ImageMapper imageMapper;
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;
    private final SurroundingAreaRepository surroundingAreaRepository;
    private final SurroundingAreaMapper surroundingAreaMapper;
    private final TargetAudienceRepository targetAudienceRepository;
    private final TargetAudienceMapper targetAudienceMapper;

    public RoomDTO saveRoom(RoomDTO roomDTO) {

        // Basic check
        if (roomDTO == null) throw new IllegalArgumentException("RoomDTO cannot be null");
        if (roomDTO.getAddress() == null) throw new IllegalArgumentException("Address is required");
        if (roomDTO.getTitle() == null || roomDTO.getTitle().isEmpty()) throw new IllegalArgumentException("Title is required");
        if (roomDTO.getDescription() == null || roomDTO.getDescription().isEmpty()) throw new IllegalArgumentException("Description is required");
        if (roomDTO.getPrice() <= 0) throw new IllegalArgumentException("Price must be positive");
        if (roomDTO.getArea() <= 0) throw new IllegalArgumentException("Area must be positive");
        if (roomDTO.getDeposit() < 0) throw new IllegalArgumentException("Deposit cannot be negative");
        if (roomDTO.getForGender() == null) throw new IllegalArgumentException("Gender type is required");
        if (roomDTO.getTotalRooms() <= 0) throw new IllegalArgumentException("Total rooms must be positive");
        if (roomDTO.getMaxPeople() <= 0) throw new IllegalArgumentException("Max people must be positive");

        if (roomDTO.getRoomType() == null) throw new IllegalArgumentException("Room type is required");

        // Check contact information
        if (roomDTO.getPosterName() == null || roomDTO.getPosterName().isEmpty()) {
            throw new IllegalArgumentException("Poster name is required");
        }

        if (roomDTO.getPosterPhone() == null || roomDTO.getPosterPhone().isEmpty()) {
            throw new IllegalArgumentException("Poster phone is required");
        }

        // Check phone number format (Vietnam)
        if (!isValidPhone(roomDTO.getPosterPhone())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        // Check room quantity fields by room type
        if (roomDTO.getRoomType() == RoomType.BOARDING_HOUSE) {
            checkNonNegative(roomDTO.getNumberOfLivingRooms(), "living rooms");
            checkNonNegative(roomDTO.getNumberOfKitchens(), "kitchens");
            checkNonNegative(roomDTO.getNumberOfBathrooms(), "bathrooms");
            checkNonNegative(roomDTO.getNumberOfBedrooms(), "bedrooms");
        } else {
            checkRequiredNonNegative(roomDTO.getNumberOfLivingRooms(), "living rooms");
            checkRequiredNonNegative(roomDTO.getNumberOfKitchens(), "kitchens");
            checkRequiredNonNegative(roomDTO.getNumberOfBathrooms(), "bathrooms");
            checkRequiredNonNegative(roomDTO.getNumberOfBedrooms(), "bedrooms");
        }

        // Check images list
        if (roomDTO.getImages() != null) {
            for (ImageDTO image : roomDTO.getImages()) {
                if (image == null) {
                    throw new IllegalArgumentException("Image in images list cannot be null");
                }
            }
        }

        // Check amenities, environments, targetAudiences và lấy dữ liệu từ database
        if (roomDTO.getAmenities() != null) {
            Set<AmenityDTO> validatedAmenities = new HashSet<>();
            for (AmenityDTO amenity : roomDTO.getAmenities()) {
                if (amenity == null) {
                    throw new IllegalArgumentException("Amenity in amenities list cannot be null");
                }
                // Verify and get from database
                Amenity dbAmenity = amenityRepository.findById(amenity.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Amenity with ID " + amenity.getId() + " not found in database"));
                validatedAmenities.add(amenityMapper.toDTO(dbAmenity));
            }
            roomDTO.setAmenities(validatedAmenities);
        }

        if (roomDTO.getSurroundingAreas() != null) {
            Set<SurroundingAreaDTO> validatedEnvironments = new HashSet<>();
            for (SurroundingAreaDTO surr : roomDTO.getSurroundingAreas()) {
                if (surr == null) {
                    throw new IllegalArgumentException("Surrounding areas in Surrounding area list cannot be null");
                }
                // Verify and get from database
                SurroundingArea dbEnvironment = surroundingAreaRepository.findById(surr.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Environment with ID " + surr.getId() + " not found in database"));
                validatedEnvironments.add(surroundingAreaMapper.toDTO(dbEnvironment));
            }
            roomDTO.setSurroundingAreas(validatedEnvironments);
        }

        if (roomDTO.getTargetAudiences() != null) {
            Set<TargetAudienceDTO> validatedTargets = new HashSet<>();
            for (TargetAudienceDTO target : roomDTO.getTargetAudiences()) {
                if (target == null) {
                    throw new IllegalArgumentException("Target audience in targetAudiences list cannot be null");
                }
                // Verify and get from database
                TargetAudience dbTarget = targetAudienceRepository.findById(target.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Target audience with ID " + target.getId() + " not found in database"));
                validatedTargets.add(targetAudienceMapper.toDTO(dbTarget));
            }
            roomDTO.setTargetAudiences(validatedTargets);
        }

        // Set create/update time
        LocalDateTime now = LocalDateTime.now();
        if (roomDTO.getId() == null) {
            // New room
            roomDTO.setCreatedAt(now);
            roomDTO.setStatus(RoomStatus.PENDING);
        }
        roomDTO.setUpdatedAt(now);

        // Call address service to insert address
        try {
            addressClient.addAddress(roomDTO.getAddress());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error when insert address!");
        }

        Room room = roomMapper.toEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDTO(savedRoom);
    }

    /**
     * Check the validity of the phone number
     * Support international and Vietnamese formats
     * @param phone The phone number to check
     * @return true if the phone number is valid, false if invalid
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;

        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");

        // International format with + sign
        if (cleanPhone.startsWith("+"))
            return cleanPhone.substring(1).matches("\\d{9,15}");

        // Vietnam phone number: 10 digits, starting with 0
        if (cleanPhone.startsWith("0") && cleanPhone.length() == 10)
            return true;

        // International Vietnam phone number: 84 + 9 digits
        if (cleanPhone.startsWith("84") && cleanPhone.length() == 11)
            return true;

        // Other cases: 9-15 numbers
        return cleanPhone.matches("\\d{9,15}");
    }

    // Check not negative (for BOARDING_HOUSE)
    private void checkNonNegative(Integer value, String fieldName) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException("Number of " + fieldName + " cannot be negative");
        }
    }

    // Mandatory and non-negative check function (for other room)
    private void checkRequiredNonNegative(Integer value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Number of " + fieldName + " is required");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Number of " + fieldName + " cannot be negative");
        }
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public List<RoomDTO> findRoomsByAddress(String street, String district, String city) {
        List<AddressDTO> addressDTOS = addressClient.search(street, district, city).getBody();

        assert addressDTOS != null;
        if (addressDTOS.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> addressIds = addressDTOS.stream().map(AddressDTO::getId).collect(Collectors.toList());
        List<Room> rooms = roomRepository.findByAddressIdIn(addressIds);

        return rooms.stream().map(room ->
                RoomDTO.builder()
                        .id(room.getId())
                        .userId(room.getUserId())
                        .title(room.getTitle())
                        .description(room.getDescription())
                        .price(room.getPrice())
                        .area(room.getArea())
                        .images(imageMapper.toDTOs(room.getImages()))
                        .status(room.getStatus())
                        .amenities(room.getAmenities().stream()
                                .map(a -> new AmenityDTO(a.getId(), a.getName()))
                                .collect(Collectors.toSet()))
                        .surroundingAreas(room.getSurroundingAreas().stream()
                                .map(e -> new SurroundingAreaDTO(e.getId(), e.getName()))
                                .collect(Collectors.toSet()))
                        .targetAudiences(room.getTargetAudiences().stream()
                                .map(t -> new TargetAudienceDTO(t.getId(), t.getName()))
                                .collect(Collectors.toSet()))
                        .createdAt(room.getCreatedAt())
                        .updatedAt(room.getUpdatedAt())
                        .address(addressDTOS.stream()
                                .filter(a -> a.getId().equals(room.getAddressId()))
                                .findFirst()
                                .orElse(null)) // Gán đúng AddressDTO
                        .build()
        ).collect(Collectors.toList());

    }
}
