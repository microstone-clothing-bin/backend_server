// 앱 설정

package com.example.clothing_backend.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration //Spring이 설정 파일로 인식
public class AppConfig {

    @Bean // 의존성 주입 가능
    public RestTemplate restTemplate() {
        return new RestTemplate(); // RestTemplate 인스턴스를 생성해서 반환 (API 통신에 사용)
    }
}