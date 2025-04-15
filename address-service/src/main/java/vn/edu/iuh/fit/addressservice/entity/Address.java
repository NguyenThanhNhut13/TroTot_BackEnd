package vn.edu.iuh.fit.addressservice.entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String province;
    private String district;
    private String ward;
    private String street;
    private String houseNumber;

    private Double latitude;
    private Double longitude;

}