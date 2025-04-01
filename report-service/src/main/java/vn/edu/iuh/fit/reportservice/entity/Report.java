package vn.edu.iuh.fit.reportservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import vn.edu.iuh.fit.reportservice.enums.ReportStatus;
import vn.edu.iuh.fit.reportservice.enums.ReportType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private Long roomId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private ReportType type;
    private String description;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


}

