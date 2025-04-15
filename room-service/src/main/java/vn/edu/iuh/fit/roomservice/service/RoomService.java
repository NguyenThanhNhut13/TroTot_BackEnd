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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.exception.RoomNotFoundException;
import vn.edu.iuh.fit.roomservice.mapper.*;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.roomservice.model.entity.*;
import vn.edu.iuh.fit.roomservice.repository.AmenityRepository;
import vn.edu.iuh.fit.roomservice.repository.RoomRepository;
import vn.edu.iuh.fit.roomservice.repository.SurroundingAreaRepository;
import vn.edu.iuh.fit.roomservice.repository.TargetAudienceRepository;

import java.time.LocalDateTime;
import java.util.*;
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
        if (roomDTO.getPrice() <= 0) throw new IllegalArgumentException("Price must be positive");
        if (roomDTO.getSelfManaged() == null) throw new IllegalArgumentException("Self-managed status must be specified");
        if (roomDTO.getArea() <= 0) throw new IllegalArgumentException("Area must be positive");
        if (roomDTO.getDeposit() < 0) throw new IllegalArgumentException("Deposit cannot be negative");
        if (roomDTO.getForGender() == null) throw new IllegalArgumentException("Gender type is required");
        if (roomDTO.getTotalRooms() <= 0) throw new IllegalArgumentException("Total rooms must be positive");
        if (roomDTO.getMaxPeople() <= 0) throw new IllegalArgumentException("Max people must be positive");
        if (roomDTO.getImages() == null) throw new IllegalArgumentException("Room image must be positive");

        // Check description at least 10 words
        if (roomDTO.getDescription() == null || roomDTO.getDescription().isBlank()) throw new IllegalArgumentException("Description is required");
        String[] words = roomDTO.getDescription().trim().split("\\s+");
        if (words.length < 10)
            throw new IllegalArgumentException("Description must contain at least 10 words");

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

        List<Image> images = new ArrayList<>();
        if (roomDTO.getImages() != null ) {
            images = roomDTO.getImages()
                    .stream().map(imageMapper::toEntity).toList();
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
        }
        roomDTO.setUpdatedAt(now);

        AddressDTO addressDTO;
        // Call address service to insert address
        try {
            ResponseEntity<BaseResponse<AddressDTO>> response = addressClient.addAddress(roomDTO.getAddress());
            addressDTO = Objects.requireNonNull(response.getBody()).getData();

        } catch (Exception e) {
            throw new InternalServerErrorException("Error when insert address!");
        }

        Room room = roomMapper.toEntity(roomDTO);
        room.setStatus(RoomStatus.PENDING);
        room.setAddressId(addressDTO.getId());

        // Assign room into each image
        for (Image image : images) {
            image.setRoom(room);
        }
        room.setImages(images);
        Room savedRoom = roomRepository.save(room);

        // Return room with address
        RoomDTO returnRoom = roomMapper.toDTO(savedRoom);
        returnRoom.setAddress(addressDTO);
        return returnRoom;
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

    public List<RoomDTO> findAllRooms() {
        return roomRepository.findAll().stream().map(roomMapper::toDTO).collect(Collectors.toList());
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

    public RoomDTO findById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new RoomNotFoundException("Room not found with id: " + id));

        AddressDTO addressDTO;
        // Call address service to get address
        try {
            ResponseEntity<BaseResponse<AddressDTO>> response = addressClient.getAddressById(room.getAddressId());
            addressDTO = Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception e) {
            throw new InternalServerErrorException("Error when get address!");
        }

        List<ImageDTO> imageDTO = room.getImages()
                .stream().map(imageMapper::toDTO)
                .toList();

        System.out.println("length: "+ imageDTO.size());

        RoomDTO roomDTO = roomMapper.toDTO(room);
        roomDTO.setAddress(addressDTO);
        roomDTO.setImages(imageDTO);

        return roomDTO;
    }
}
