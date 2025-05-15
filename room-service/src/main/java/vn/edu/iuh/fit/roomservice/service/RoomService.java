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

import feign.FeignException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.client.UserClient;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.exception.BadRequestException;
import vn.edu.iuh.fit.roomservice.exception.RoomNotFoundException;
import vn.edu.iuh.fit.roomservice.mapper.*;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.roomservice.model.dto.response.PageResponse;
import vn.edu.iuh.fit.roomservice.model.entity.*;
import vn.edu.iuh.fit.roomservice.repository.*;
import vn.edu.iuh.fit.roomservice.repository.spec.RoomSpecification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AddressIntegrationService addressIntegrationService;
    private final RoomMapper roomMapper;
    private final ImageMapper imageMapper;
    private final AmenityRepository amenityRepository;
    private final SurroundingAreaRepository surroundingAreaRepository;
    private final TargetAudienceRepository targetAudienceRepository;
    private final UserClient userClient;
    private final ImageRepository imageRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoomDTO saveRoom(RoomDTO roomDTO) {
        // Check input data
        validateRoomDTO(roomDTO);

        // Set create/update time
        LocalDateTime now = LocalDateTime.now();
        roomDTO.setCreatedAt(now);
        roomDTO.setUpdatedAt(now);

        // Call address service to insert address
        AddressDTO addressDTO = saveOrUpdateAddress(null, roomDTO.getAddress());

        Room room = roomMapper.toEntity(roomDTO);
        room.setStatus(RoomStatus.PENDING);
        room.setAddressId(addressDTO.getId());

        // Prepare data
        room = prepareRoomData(roomDTO, room);

        // Save room
        Room savedRoom = roomRepository.save(room);

        try {
            userClient.usePostSlot();
        } catch (FeignException e) {
            // Check for 400 error (Bad Request) from users-service
            if (e.status() == 400) {
                // Extract error message from service (JSON automatically converted to string)
                String message = e.contentUTF8();
                throw new BadRequestException(message);
            }
            // For non-400 errors, throw general error with different codes
            throw new InternalServerErrorException("Error from user service: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new InternalServerErrorException("Unexpected error when posting room!");
        }

        // Return room with address
        RoomDTO returnRoom = roomMapper.toDTO(savedRoom);
        returnRoom.setAddress(addressDTO);
        return returnRoom;
    }

    public RoomDTO updateRoom(Long roomId, RoomDTO roomDTO) {
        // Check input data
        validateRoomDTO(roomDTO);

        // Get room with id
        Room existingRoom = getRoomById(roomId);

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
        AddressDTO addressDTO = saveOrUpdateAddress(existingRoom.getAddressId(), roomDTO.getAddress());
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

    public RoomDTO findById(Long id) {
        Room room = getRoomById(id);
        AddressDTO addressDTO = getAddressById(room.getAddressId());

        List<ImageDTO> imageDTO = room.getImages()
                .stream().map(imageMapper::toDTO)
                .toList();

        RoomDTO roomDTO = roomMapper.toDTO(room);
        roomDTO.setAddress(addressDTO);
        roomDTO.setImages(imageDTO);

        return roomDTO;
    }

    public PageResponse<RoomListDTO> findAllRooms(int page, int size, String sort, RoomType roomType) {
        long totalStart = System.currentTimeMillis();

        long startFetchRoom = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Room> roomPage;

        if (roomType != null) {
            roomPage = roomRepository.findByRoomType(roomType, pageable);
        } else {
            roomPage = roomRepository.findAllRoom(pageable);
        }

//        System.out.println("⏱️ Time to fetch Room from DB: " + (System.currentTimeMillis() - startFetchRoom) + "ms");

        long startBuildRoomPage = System.currentTimeMillis();
        PageResponse<RoomListDTO> response = buildRoomPageResponse(roomPage);
//        System.out.println("⏱️ Time to build Room Page (with addresses): " + (System.currentTimeMillis() - startBuildRoomPage) + "ms");

//        System.out.println("✅ Total time for findAllRooms: " + (System.currentTimeMillis() - totalStart) + "ms");

        return response;
    }

    public PageResponse<RoomListDTO> searchRooms(
            int page, int size, String sortParam,
            String street, String district, String city,
            Double minPrice, Double maxPrice,
            String areaRange, String roomType,
            List<String> amenityNames, List<String> environmentNames,
            List<String> targetAudienceNames, Boolean hasVideoReview
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sortParam));

        // Get addresses and create map for quick lookup
        Map<Long, AddressDTO> addressMap = fetchAddressMap(street, district, city);

        List<Long> addressIds = (addressMap != null && !addressMap.isEmpty())
                ? new ArrayList<>(addressMap.keySet())
                : null;

        Specification<Room> spec = RoomSpecification.buildSpecification(
                addressIds, minPrice, maxPrice, areaRange, roomType,
                amenityNames, environmentNames, targetAudienceNames, hasVideoReview
        );


        long startFetchImages = System.currentTimeMillis();
        Page<Room> roomPage = roomRepository.findAll(spec, pageable);
        System.out.println("⏱️ Time to fetch Room for search: " + (System.currentTimeMillis() - startFetchImages) + "ms");

        // Build response with address info
        return buildRoomPageResponse(roomPage);
    }

    public List<RoomTrainDTO> exportAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        // Get addressId list from each room
        List<Long> addressIds = rooms.stream()
                .map(Room::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Get addresses for all rooms
        Map<Long, AddressSummaryDTO> addressMap = getAddressMapForRooms(addressIds);

        return rooms.stream()
                .map(room -> {
                    RoomTrainDTO dto = roomMapper.toRoomTrainDTO(room);
                    AddressSummaryDTO addressDTO = addressMap.get(room.getAddressId());
                    if (addressDTO != null) {
                        dto.setDistrict(addressDTO.getDistrict());
                        dto.setProvince(addressDTO.getProvince());
                    }
                    return dto;
                })
                .toList();
    }

    public List<RoomListDTO> findByIds(List<Long> ids) {
        long startFetchRooms = System.currentTimeMillis();
        List<Room> rooms = roomRepository.findByIds(ids);
        System.out.println("⏱️ Time to fetch Room for search: " + (System.currentTimeMillis() - startFetchRooms) + "ms");

        // Get addressId list from each room
        List<Long> addressIds = rooms.stream()
                .map(Room::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Call service to get map from addressId -> AddressSummaryDTO
        Map<Long, AddressSummaryDTO> addressMap = getAddressMapForRooms(addressIds);

        long startFetchImages = System.currentTimeMillis();
        List<Image> images = imageRepository.findFirstImagesByRoomIds(ids);
        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        img -> img.getRoom().getId(),
                        Image::getImageUrl,
                        (existing, replacement) -> existing // Giữ URL đầu tiên nếu trùng
                ));
        System.out.println("⏱️ Time to fetch Images: " + (System.currentTimeMillis() - startFetchImages) + "ms");

        return rooms.stream()
                .map(room -> {
                    RoomListDTO dto = roomMapper.toListDTO(room);
                    AddressSummaryDTO addressDTO = addressMap.get(room.getAddressId());
                    if (addressDTO != null) {
                        dto.setDistrict(addressDTO.getDistrict());
                        dto.setProvince(addressDTO.getProvince());
                    }

                    // Set image URL if available
                    String imageUrl = imageMap.get(room.getId());
                    if (imageUrl != null) {
                        dto.setImageUrls(Collections.singletonList(imageUrl)); // Assuming only one image is needed
                    }
                    return dto;
                })
                .toList();
    }

    public boolean checkRoomExistsById(Long roomId) {
        boolean exists = roomRepository.existsById(roomId);
        if (!exists) {
            throw new RoomNotFoundException("Room not found with ID: " + roomId);
        }
        return true;
    }

    // ====================== HELPER METHODS ======================

    /**
     * Validates the RoomDTO for required fields and valid values
     */
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

    /**
     * Prepares room data by setting related entities
     */
    private Room prepareRoomData(RoomDTO roomDTO, Room room) {
        // Image processing
        Set<Image> images = roomDTO.getImages().stream().map(imageMapper::toEntity).collect(Collectors.toSet());
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

        // SurroundingArea processing
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

    /**
     * Parse sort string into Sort object
     */
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

    /**
     * Gets a room by ID or throws exception if not found
     */
    private Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new RoomNotFoundException("Room not found with id: " + id));
    }

    /**
     * Gets an address by ID from the address service
     * With fallback handling for exceptions
     */
    public AddressDTO getAddressById(Long addressId) {
        ResponseEntity<BaseResponse<AddressDTO>> response = addressIntegrationService.getAddressById(addressId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    /**
     * Save or update address through address service
     */
    private AddressDTO saveOrUpdateAddress(Long addressId, AddressDTO addressDTO) {
        ResponseEntity<BaseResponse<AddressDTO>> response;
        if (addressId == null) {
            // Create new address
            response = addressIntegrationService.addAddress(addressDTO);
        } else {
            // Update existing address
            response = addressIntegrationService.updateAddress(addressId, addressDTO);
        }
        return Objects.requireNonNull(response.getBody()).getData();
    }

    /**
     * Gets address map for search filters
     */
    private Map<Long, AddressDTO> fetchAddressMap(String street, String district, String city) {
        if (street == null && district == null && city == null) return null;

        ResponseEntity<BaseResponse<List<AddressDTO>>> response = addressIntegrationService.searchAddresses(street, district, city);

        return Optional.ofNullable(response)
                .filter(res -> res.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(BaseResponse::getData)
                .orElse(List.of()) // fallback empty
                .stream()
                .collect(Collectors.toMap(AddressDTO::getId, Function.identity()));

        return Collections.emptyMap();
    }

    /**
     * Builds page response for room listings
     */
    private PageResponse<RoomListDTO> buildRoomPageResponse(Page<Room> roomPage) {

        long start = System.currentTimeMillis();

        // Lấy danh sách roomId
        List<Long> roomIds = roomPage.getContent().stream()
                .map(Room::getId)
                .toList();

        // Lấy ảnh batch
        long startFetchImages = System.currentTimeMillis();
        List<Image> images = imageRepository.findFirstImagesByRoomIds(roomIds);
        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        img -> img.getRoom().getId(),
                        Image::getImageUrl,
                        (existing, replacement) -> existing // Giữ URL đầu tiên nếu trùng
                ));
//        System.out.println("⏱️ Time to fetch Images: " + (System.currentTimeMillis() - startFetchImages) + "ms");

        // Lấy danh sách addressId
        List<Long> addressIds = roomPage.getContent().stream()
                .map(Room::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
//        System.out.println("⏱️ Time to extract Address IDs: " + (System.currentTimeMillis() - start) + "ms");

        // Gọi Address Service
        start = System.currentTimeMillis();
        Map<Long, AddressSummaryDTO> addressMap = addressIds.isEmpty() ? Map.of() : getAddressMapForRooms(addressIds);
//        System.out.println("⏱️ Time to fetch all Addresses: " + (System.currentTimeMillis() - start) + "ms");

        // Map sang RoomListDTO
        start = System.currentTimeMillis();
        List<RoomListDTO> roomDTOs = roomPage.getContent().stream()
                .map(room -> {
                    RoomListDTO dto = RoomListDTO.builder()
                            .id(room.getId())
                            .title(room.getTitle())
                            .price(room.getPrice())
                            .area(room.getArea())
                            .roomType(room.getRoomType())
                            .imageUrls(imageMap.containsKey(room.getId()) ?
                                    List.of(imageMap.get(room.getId())) : List.of())
                            .build();
                    AddressSummaryDTO address = addressMap.get(room.getAddressId());
                    if (address != null) {
                        dto.setDistrict(address.getDistrict());
                        dto.setProvince(address.getProvince());
                    }
                    return dto;
                })
                .toList();
        System.out.println("⏱️ Time to build RoomListDTO with AddressMap: " + (System.currentTimeMillis() - start) + "ms");

        // Tạo PageResponse
        return PageResponse.<RoomListDTO>builder()
                .content(roomDTOs)
                .page(roomPage.getNumber())
                .size(roomPage.getSize())
                .totalElements(roomPage.getTotalElements())
                .totalPages(roomPage.getTotalPages())
                .last(roomPage.isLast())
                .build();
    }

    /**
     * Gets all addresses for a list of rooms
     */
    private Map<Long, AddressSummaryDTO> getAddressMapForRooms(List<Long> addressIds) {
        long start = System.currentTimeMillis();
        BaseResponse<List<AddressSummaryDTO>> response = addressIntegrationService.getAddressSummary(addressIds).getBody();
        System.out.println("⏱️ Time to call addressClient.getAddressesByIds: " + (System.currentTimeMillis() - start) + "ms");
        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(AddressSummaryDTO::getId, dto -> dto));
        }
        System.out.println("Testttttttttttttttttttttt");
        return Map.of();
    }

    public void updateVideoReview(Long roomId, String videoUrl) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        room.setVideoUrl(videoUrl);
        roomRepository.save(room);
    }
}