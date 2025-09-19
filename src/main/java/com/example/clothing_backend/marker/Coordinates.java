package com.example.clothing_backend.marker;

// 위도와 경도를 담기 위한 record (Java 14 이상)
// record를 사용하면 생성자, getter, equals, hashCode, toString을 자동으로 만들어준다.
public record Coordinates(double latitude, double longitude) {
}