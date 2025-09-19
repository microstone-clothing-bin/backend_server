package com.example.clothing_backend.marker;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
public class ClothingBinService {

    private final ClothingBinRepository clothingBinRepository;

    public ClothingBinService(ClothingBinRepository clothingBinRepository) {
        this.clothingBinRepository = clothingBinRepository;
    }

    // 컨트롤러로부터 받은 파라미터로 비즈니스 로직 처리 (반경 검색)
    public List<ClothingBin> findClothingBins(Double lat, Double lng, Double radiusKm) {
        if (lat != null && lng != null && radiusKm != null) {
            // 위치 기반 검색 로직
            return clothingBinRepository.findBinsWithinRadius(lat, lng, radiusKm);
        } else {
            // 전체 검색 로직
            return clothingBinRepository.findAll();
        }
    }

    // 사각형 경계 좌표 받아서 리포에 전달
    public List<ClothingBin> findBinsInBounds(double swLat, double swLng, double neLat, double neLng) {
        return clothingBinRepository.findBinsInBounds(swLat, swLng, neLat, neLng);
    }
}