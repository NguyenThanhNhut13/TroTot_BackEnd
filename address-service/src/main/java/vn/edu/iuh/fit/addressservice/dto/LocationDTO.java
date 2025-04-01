package vn.edu.iuh.fit.addressservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private String wardName;
    private String wardCode;
    private String districtName;
    private String districtCode;
    private String provinceName;
    private String provinceCode;
}
