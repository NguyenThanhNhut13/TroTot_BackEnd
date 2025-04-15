package vn.edu.iuh.fit.addressservice.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Long id;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String houseNumber;
    private Double latitude;
    private Double longitude;
}