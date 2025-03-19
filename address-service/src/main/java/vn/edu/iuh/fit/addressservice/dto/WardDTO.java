package vn.edu.iuh.fit.addressservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WardDTO {
    @JsonProperty("_id") // Ánh xạ từ `_id`
    private String id;

    private String code;
    private String name;
    private String slug;
    private String type;

    @JsonProperty("name_with_type")
    private String nameWithType;

    private String path;

    @JsonProperty("path_with_type")
    private String pathWithType;

    @JsonProperty("isDeleted")
    private boolean deleted;

    @JsonProperty("parent_code") // API trả về "parent_code", cần ánh xạ vào districtCode
    private String districtCode;
}
