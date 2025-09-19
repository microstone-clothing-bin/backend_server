// MySQL 버전 8.0 이상이여야 함

package com.example.clothing_backend.marker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ClothingBinRepository extends JpaRepository<ClothingBin, Long> {

    // 데이터 로딩 시 중복 체크를 위한 메소드
    @Query("SELECT new com.example.clothing_backend.marker.Coordinates(c.latitude, c.longitude) FROM ClothingBin c")
    Set<Coordinates> findAllCoordinates();

    boolean existsByLatitudeAndLongitude(double latitude, double longitude);

    // MySQL 8.0 이상에서 지원하는 ST_Distance_Sphere 함수를 사용한 성능 최적화 쿼리
    // 이 함수는 거리를 미터(m) 단위로 계산하므로, 파라미터로 받은 킬로미터(km)에 1000을 곱해줘야 한다
    @Query(value = "SELECT * FROM clothing_bin c " +
            "WHERE ST_Distance_Sphere(point(c.longitude, c.latitude), point(:lng, :lat)) <= :radiusKm * 1000 " +
            "ORDER BY ST_Distance_Sphere(point(c.longitude, c.latitude), point(:lng, :lat))",
            nativeQuery = true)
    List<ClothingBin> findBinsWithinRadius(@Param("lat") double lat,
                                           @Param("lng") double lng,
                                           @Param("radiusKm") double radiusKm);

    // 지도 현재 위치 사격형 경계 내 데이터 조회
    @Query(value = "SELECT * FROM clothing_bin " +
            "WHERE latitude BETWEEN :swLat AND :neLat " +
            "AND longitude BETWEEN :swLng AND :neLng",
            nativeQuery = true)
    List<ClothingBin> findBinsInBounds(@Param("swLat") double swLat,
                                       @Param("swLng") double swLng,
                                       @Param("neLat") double neLat,
                                       @Param("neLng") double neLng);
}