package vn.edu.iuh.fit.addressservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    @JsonProperty("_id") // Ánh xạ từ `_id`
    private String id;

    private String code;
    private String name;
    private String slug;
    private String type;

    @JsonProperty("name_with_type") // Ánh xạ từ "name_with_type"
    private String nameWithType;

    @JsonProperty("isDeleted") // API trả về "isDeleted", cần ánh xạ vào deleted
    private boolean deleted;
}
