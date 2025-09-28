package com.example.clothing_backend.marker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClothingBinRepository extends JpaRepository<ClothingBin, Long> {

    // 데이터 로딩 시 중복 좌표를 체크하기 위한 쿼리
    @Query("SELECT new com.example.clothing_backend.marker.Coordinates(c.latitude, c.longitude) FROM ClothingBin c")
    Set<Coordinates> findAllCoordinates();

    // 특정 좌표에 의류수거함이 이미 존재하는지 확인
    boolean existsByLatitudeAndLongitude(double latitude, double longitude);

    // 반경 검색: MySQL의 ST_Distance_Sphere 함수를 사용 (거리 단위: 미터)
    // [핵심 수정] DB 함수가 요구하는 경도(lng), 위도(lat) 순서로 파라미터를 받도록 정의합니다.
    @Query(value = "SELECT * FROM clothing_bin c " +
            "WHERE ST_Distance_Sphere(point(c.longitude, c.latitude), point(:lng, :lat)) <= :radiusKm * 1000 " +
            "ORDER BY ST_Distance_Sphere(point(c.longitude, c.latitude), point(:lng, :lat))",
            nativeQuery = true)
    List<ClothingBin> findBinsWithinRadius(@Param("lng") double lng,
                                           @Param("lat") double lat,
                                           @Param("radiusKm") double radiusKm);

    // 지도 사각형 경계 내 데이터 조회
    @Query(value = "SELECT * FROM clothing_bin " +
            "WHERE latitude BETWEEN :swLat AND :neLat " +
            "AND longitude BETWEEN :swLng AND :neLng",
            nativeQuery = true)
    List<ClothingBin> findBinsInBounds(@Param("swLat") double swLat,
                                       @Param("swLng") double swLng,
                                       @Param("neLat") double neLat,
                                       @Param("neLng") double neLng);
}