package vn.edu.iuh.fit.roomservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.roomservice.model.entity.Image;

import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Long> {

  @Query("""
      SELECT new vn.edu.iuh.fit.roomservice.model.entity.Image(
          i.id, i.imageUrl, i.room.id
      )
      FROM Image i
      WHERE i.room.id IN :roomIds AND i.id IN (
          SELECT MIN(ii.id)
          FROM Image ii
          WHERE ii.room.id IN :roomIds
          GROUP BY ii.room.id
      )
  """)
  List<Image> findFirstImagesByRoomIds(@Param("roomIds") List<Long> roomIds);


}