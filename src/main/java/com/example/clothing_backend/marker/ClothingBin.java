package com.example.clothing_backend.marker;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clothing_bin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothingBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_address")
    private String roadAddress;

    @Column(name = "land_lot_address")
    private String landLotAddress;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public ClothingBin(String roadAddress, String landLotAddress, double latitude, double longitude) {
        this.roadAddress = roadAddress;
        this.landLotAddress = landLotAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}