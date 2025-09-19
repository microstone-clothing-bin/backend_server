package com.example.clothing_backend.marker;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clothing-bins")
@CrossOrigin(origins = "*")
public class ClothingBinController {

    private final ClothingBinService clothingBinService;

    public ClothingBinController(ClothingBinService clothingBinService) {
        this.clothingBinService = clothingBinService;
    }

    // 전체 조회 및 반경 검색 API (GET /api/clothing-bins)
    @GetMapping(produces = "application/json; charset=UTF-8")
    public List<ClothingBin> getClothingBins(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm) {
        // 모든 로직을 Service에 위임하고, 결과만 받아서 반환한다.
        return clothingBinService.findClothingBins(lat, lng, radiusKm);
    }

    // 프론트엔드에서 귀퉁이 좌표를 받아 사각형 내의 데이터를 조회하는 새로운 API (swLat,Lng / neLat,Lng) (추후 버튼을 눌러, 현 위치로 조회하는 기능 추가해야함)
    @GetMapping(value = "/in-bounds", produces = "application/json; charset=UTF-8")
    public List<ClothingBin> getBinsInBounds(
            @RequestParam double swLat,
            @RequestParam double swLng,
            @RequestParam double neLat,
            @RequestParam double neLng) {
        return clothingBinService.findBinsInBounds(swLat, swLng, neLat, neLng);
    }
}