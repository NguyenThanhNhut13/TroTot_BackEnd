/*
 * @ (#) Image.java       1.0     11/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/04/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Image(Long id, String imageUrl, Long roomId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.room = new Room();
        this.room.setId(roomId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;
        return id != null && id.equals(((Image) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

