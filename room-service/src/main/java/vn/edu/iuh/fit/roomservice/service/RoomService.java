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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.exception.RoomNotFoundException;
import vn.edu.iuh.fit.roomservice.mapper.*;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.roomservice.model.dto.response.PageResponse;
import vn.edu.iuh.fit.roomservice.model.entity.*;
import vn.edu.iuh.fit.roomservice.repository.AmenityRepository;
import vn.edu.iuh.fit.roomservice.repository.RoomRepository;
import vn.edu.iuh.fit.roomservice.repository.SurroundingAreaRepository;
import vn.edu.iuh.fit.roomservice.repository.TargetAudienceRepository;
import vn.edu.iuh.fit.roomservice.repository.spec.RoomSpecification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
        // Check input data
        validateRoomDTO(roomDTO);

        // Set create/update time
        LocalDateTime now = LocalDateTime.now();
        roomDTO.setCreatedAt(now);
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

        // Prepare data
        room = prepareRoomData(roomDTO, room);

        // Save room
        Room savedRoom = roomRepository.save(room);

        // Return room with address
        RoomDTO returnRoom = roomMapper.toDTO(savedRoom);
        returnRoom.setAddress(addressDTO);
        return returnRoom;
    }

    private void validateRoomDTO(RoomDTO roomDTO) {
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

        // Check amenities, environments, targetAudiences and get data from database
        if (roomDTO.getAmenities() != null) {
            for (AmenityDTO amenity : roomDTO.getAmenities()) {
                if (amenity == null) throw new IllegalArgumentException("Amenity in amenities list cannot be null");
            }
        }
        if (roomDTO.getSurroundingAreas() != null) {
            for (SurroundingAreaDTO surr : roomDTO.getSurroundingAreas()) {
                if (surr == null) throw new IllegalArgumentException("Surrounding area in surrounding areas list cannot be null");
            }
        }
        if (roomDTO.getTargetAudiences() != null) {
            for (TargetAudienceDTO target : roomDTO.getTargetAudiences()) {
                if (target == null) throw new IllegalArgumentException("Target audience in targetAudiences list cannot be null");
            }
        }
    }

    private Room prepareRoomData(RoomDTO roomDTO, Room room) {
        // Image processing
        List<Image> images = roomDTO.getImages().stream().map(imageMapper::toEntity).toList();
        for (Image image : images) {
            image.setRoom(room);
        }
        room.setImages(images);

        // Amenities processing
        if (roomDTO.getAmenities() != null) {
            Set<Amenity> amenities = roomDTO.getAmenities().stream()
                    .map(amenityDTO -> amenityRepository.findById(amenityDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Amenity with ID " + amenityDTO.getId() + " not found")))
                    .collect(Collectors.toSet());
            room.setAmenities(amenities);
        }

        // SurrondingArea processing
        if (roomDTO.getSurroundingAreas() != null) {
            Set<SurroundingArea> surroundingAreas = roomDTO.getSurroundingAreas().stream()
                    .map(surrDTO -> surroundingAreaRepository.findById(surrDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Surrounding area with ID " + surrDTO.getId() + " not found")))
                    .collect(Collectors.toSet());
            room.setSurroundingAreas(surroundingAreas);
        }

        // Target audience processing
        if (roomDTO.getTargetAudiences() != null) {
            Set<TargetAudience> targetAudiences = roomDTO.getTargetAudiences().stream()
                    .map(targetDTO -> targetAudienceRepository.findById(targetDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Target audience with ID " + targetDTO.getId() + " not found")))
                    .collect(Collectors.toSet());
            room.setTargetAudiences(targetAudiences);
        }

        return room;
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

    public PageResponse<RoomDTO> findAllRooms(int page, int size, String sort, RoomType roomType) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        Page<Room> roomPage;

        if (roomType != null) {
            roomPage = roomRepository.findByRoomType(roomType, pageable);
        } else {
            roomPage = roomRepository.findAll(pageable);
        }

        List<RoomDTO> roomDTOs = roomPage.getContent().stream()
                .map(room -> {
                    RoomDTO dto = roomMapper.toDTO(room);
                    try {
                        ResponseEntity<BaseResponse<AddressDTO>> response = addressClient.getAddressById(room.getAddressId());
                        AddressDTO addressDTO = Objects.requireNonNull(response.getBody()).getData();
                        dto.setAddress(addressDTO);
                    } catch (Exception e) {
                        // Gọi thất bại thì gán null (đã là mặc định nếu chưa set gì rồi)
                        dto.setAddress(null);
                        // Log lỗi nếu cần theo dõi
                        System.err.println("Could not fetch address for roomId: " + room.getId() + ", addressId: " + room.getAddressId() + ", message: " + e.getMessage());
                    }
                    return dto;
                })
                .toList();


        return PageResponse.<RoomDTO>builder()
                .content(roomDTOs)
                .page(roomPage.getNumber())
                .size(roomPage.getSize())
                .totalElements(roomPage.getTotalElements())
                .totalPages(roomPage.getTotalPages())
                .last(roomPage.isLast())
                .build();
    }

   // Sort
    private Sort parseSort(String sort) {
        // Default sort createAt desc if not have value
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        // If there are multiple sort fields, separate them with semicolons
        // Example: "createdAt,desc;name,asc"
        String[] sortCriteria = sort.split(";");
        List<Sort.Order> orders = new ArrayList<>();

        for (String criteria : sortCriteria) {
            String[] parts = criteria.split(",");
            String property = parts[0].trim();

            if (parts.length > 1) {
                // If there is a direction of arrangement specified
                Sort.Direction direction = "desc".equalsIgnoreCase(parts[1].trim()) ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, property));
            } else {
                // Default if not specify direction
                orders.add(new Sort.Order(Sort.Direction.ASC, property));
            }
        }

        return Sort.by(orders);
    }


//    public List<RoomDTO> findRoomsByAddress(String street, String district, String city) {
//        List<AddressDTO> addressDTOS = addressClient.search(street, district, city).getBody();
//
//        assert addressDTOS != null;
//        if (addressDTOS.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<Long> addressIds = addressDTOS.stream().map(AddressDTO::getId).collect(Collectors.toList());
//        List<Room> rooms = roomRepository.findByAddressIdIn(addressIds);
//
//        return rooms.stream().map(room ->
//                RoomDTO.builder()
//                        .id(room.getId())
//                        .userId(room.getUserId())
//                        .title(room.getTitle())
//                        .description(room.getDescription())
//                        .price(room.getPrice())
//                        .area(room.getArea())
//                        .images(imageMapper.toDTOs(room.getImages()))
//                        .amenities(room.getAmenities().stream()
//                                .map(a -> new AmenityDTO(a.getId(), a.getName()))
//                                .collect(Collectors.toSet()))
//                        .surroundingAreas(room.getSurroundingAreas().stream()
//                                .map(e -> new SurroundingAreaDTO(e.getId(), e.getName()))
//                                .collect(Collectors.toSet()))
//                        .targetAudiences(room.getTargetAudiences().stream()
//                                .map(t -> new TargetAudienceDTO(t.getId(), t.getName()))
//                                .collect(Collectors.toSet()))
//                        .createdAt(room.getCreatedAt())
//                        .updatedAt(room.getUpdatedAt())
//                        .address(addressDTOS.stream()
//                                .filter(a -> a.getId().equals(room.getAddressId()))
//                                .findFirst()
//                                .orElse(null)) // Gán đúng AddressDTO
//                        .build()
//        ).collect(Collectors.toList());
//
//    }

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

        RoomDTO roomDTO = roomMapper.toDTO(room);
        roomDTO.setAddress(addressDTO);
        roomDTO.setImages(imageDTO);

        return roomDTO;
    }

    public RoomDTO updateRoom(Long roomId, RoomDTO roomDTO) {
        // Check input data
        validateRoomDTO(roomDTO);

        // Get room with id
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room with ID " + roomId + " not found"));

        // Convert DTO to entity
        Room updatedRoom = roomMapper.toEntity(roomDTO);
        updatedRoom.setId(existingRoom.getId());
        updatedRoom.setCreatedAt(existingRoom.getCreatedAt());
        updatedRoom.setUpdatedAt(LocalDateTime.now());
        updatedRoom.setStatus(existingRoom.getStatus());

        RoomDetail existingRoomDetail = existingRoom.getRoomDetail();
        if (existingRoomDetail != null) {
            // Update RoomDetail existing
            existingRoomDetail.setNumberOfLivingRooms(updatedRoom.getRoomDetail().getNumberOfLivingRooms());
            existingRoomDetail.setNumberOfKitchens(updatedRoom.getRoomDetail().getNumberOfKitchens());
            existingRoomDetail.setNumberOfBathrooms(updatedRoom.getRoomDetail().getNumberOfBathrooms());
            existingRoomDetail.setNumberOfBedrooms(updatedRoom.getRoomDetail().getNumberOfBedrooms());
            updatedRoom.setRoomDetail(existingRoomDetail);
        } else {
            // If there is no RoomDetail, assign a new RoomDetail
            updatedRoom.setRoomDetail(updatedRoom.getRoomDetail());
        }

        // Address processing
        AddressDTO addressDTO;
        try {
            ResponseEntity<BaseResponse<AddressDTO>> response = addressClient.updateAddress(existingRoom.getAddressId(), roomDTO.getAddress());
            addressDTO = Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception e) {
            throw new InternalServerErrorException("Error when updating address!");
        }
        updatedRoom.setAddressId(addressDTO.getId());

        // Prepare data
        updatedRoom = prepareRoomData(roomDTO, updatedRoom);

        // Save
        Room savedRoom = roomRepository.save(updatedRoom);

        // Return DTO
        RoomDTO returnRoom = roomMapper.toDTO(savedRoom);
        returnRoom.setAddress(addressDTO);
        return returnRoom;
    }

    // Filter search
    public PageResponse<RoomDTO> searchRooms(
            int page, int size, String sortParam,
            String street, String district, String city,
            Double minPrice, Double maxPrice,
            String areaRange, String roomType,
            List<Long> amenityIds, List<Long> environmentIds,
            List<Long> targetAudienceIds
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sortParam));

        // Lấy danh sách địa chỉ và tạo Map để tra nhanh
        Map<Long, AddressDTO> addressMap = fetchAddressMap(street, district, city);

        List<Long> addressIds = (addressMap != null && !addressMap.isEmpty())
                ? new ArrayList<>(addressMap.keySet())
                : null;

        Specification<Room> spec = RoomSpecification.buildSpecification(
                addressIds, minPrice, maxPrice, areaRange, roomType,
                amenityIds, environmentIds, targetAudienceIds
        );

        Page<Room> roomPage = roomRepository.findAll(spec, pageable);

        List<RoomDTO> roomDTOs = roomPage.getContent()
                .stream()
                .map(room -> {
                    RoomDTO dto = roomMapper.toDTO(room);
                    AddressDTO addressDTO = addressMap != null ? addressMap.get(room.getAddressId()) : null;
                    dto.setAddress(addressDTO);
                    return dto;
                })
                .toList();

        return PageResponse.<RoomDTO>builder()
                .content(roomDTOs)
                .page(roomPage.getNumber())
                .size(roomPage.getSize())
                .totalElements(roomPage.getTotalElements())
                .totalPages(roomPage.getTotalPages())
                .last(roomPage.isLast())
                .build();
    }

    private Map<Long, AddressDTO> fetchAddressMap(String street, String district, String city) {
        if (street == null && district == null && city == null) return null;

        ResponseEntity<BaseResponse<List<AddressDTO>>> response = addressClient.searchAddresses(street, district, city);

        return Optional.ofNullable(response)
                .filter(res -> res.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(BaseResponse::getData)
                .orElse(List.of()) // fallback empty
                .stream()
                .collect(Collectors.toMap(AddressDTO::getId, Function.identity()));
    }




}
