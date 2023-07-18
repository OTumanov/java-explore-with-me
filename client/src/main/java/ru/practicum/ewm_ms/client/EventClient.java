package ru.practicum.ewm_ms.client;

import dto.HitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventClient {
    public static final String API_HIT_PREFIX = "/hit";
    public static final String APP_NAME = "ewm-service";
    public static final String API_STATS_PREFIX = "/stats";
    public static final String BASE_PATH = "jdbc:postgresql://localhost:5432/ewm_stats";

    private final RestTemplate hitRest;
    private final RestTemplate statsRest;

    @Autowired
    public EventClient(@Value("http://localhost/8080") String serverUrl, RestTemplateBuilder builder) {
        hitRest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_HIT_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        statsRest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_STATS_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void postHit(String endpoint, String clientIp) {
        HitDto dto = new HitDto(APP_NAME, endpoint, clientIp, DateTimeMapper.toString(LocalDateTime.now()));
        HttpEntity<HitDto> requestEntity = new HttpEntity<>(dto);
        String path = BASE_PATH + API_HIT_PREFIX;
        hitRest.exchange(path, HttpMethod.POST, requestEntity, HitDto.class);
    }
}