package vn.edu.iuh.fit.addressservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ward {
    @Id
    @Column(name = "_id", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String id;

    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String slug;

    @Column(columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String type;

    @Column(name = "name_with_type", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nameWithType;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String path;

    @Column(name = "path_with_type", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String pathWithType;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}
